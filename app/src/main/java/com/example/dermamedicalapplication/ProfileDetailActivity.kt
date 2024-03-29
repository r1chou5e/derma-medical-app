package com.example.dermamedicalapplication

import android.R
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dermamedicalapplication.databinding.ActivityProfileDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var calendar: Calendar

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)

        firebaseAuth = FirebaseAuth.getInstance()

        // laod Dob
        calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dobEt.setText(dateFormatter.format(calendar.time))


        // load Gender
        val genders = arrayOf("Chưa xác định", "Nam", "Nữ")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        var genderSpinner = binding.genderSpinner
        genderSpinner.adapter = adapter

        val defaultGender = "Chưa xác định"
        val defaultGenderPosition = genders.indexOf(defaultGender)
        if (defaultGenderPosition != -1) {
            genderSpinner.setSelection(defaultGenderPosition)
        }

        genderSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGender = genders[position]
                if (selectedGender != "Nam" && selectedGender != "Nữ") {
                    genderSpinner.setSelection(defaultGenderPosition)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.editBtn.setOnClickListener {
            binding.fullnameEt.isEnabled = true
            binding.phoneEt.isEnabled = true
            binding.dobEt.isEnabled = true
            binding.genderSpinner.isEnabled = true
        }

        binding.updateBtn.setOnClickListener {
            updateInfo()
        }

    }

    private fun updateInfo() {
        val uid = firebaseAuth.currentUser?.uid

        if (uid != null) {
            progressDialog.setMessage("Đang lưu thông tin người dùng...")
            progressDialog.show()

            // Setup data to add in db
            val hashMap: HashMap<String, Any?> = HashMap()
            hashMap["email"] = binding.emailEt.text.toString().trim()
            hashMap["fullname"] = binding.fullnameEt.text.toString().trim()
            hashMap["phoneNumber"] = binding.phoneEt.text.toString().trim()
            hashMap["dob"] = binding.dobEt.text.toString().trim()
            hashMap["gender"] = binding.genderSpinner.selectedItem.toString()

            val ref = FirebaseDatabase.getInstance().getReference("User").child(uid)
            ref.updateChildren(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Cập nhật thông tin người dùng thành công !", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
                .addOnFailureListener {e ->
                    // failed adding data to db
                    progressDialog.dismiss()
                    Toast.makeText(this, "Cập nhật thông tin người dùng thất bại !!", Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun loadUserInfo() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            onBackPressed()
        }
        else {
            val ref = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserModel::class.java)
                        binding.emailEt.setText(user?.email.toString())
                        binding.fullnameEt.setText(user?.fullname.toString())
                        binding.phoneEt.setText(user?.phoneNumber.toString())
                        binding.dobEt.setText(user?.dob.toString())

                        val gender = user?.gender.toString()
                        if (gender == "Nam") binding.genderSpinner.setSelection(1)
                        else if (gender == "Nữ") binding.genderSpinner.setSelection(2)
                        else {
                            binding.genderSpinner.setSelection(0)
                        }

                        binding.emailEt.isEnabled = false
                        binding.fullnameEt.isEnabled = false
                        binding.phoneEt.isEnabled = false
                        binding.dobEt.isEnabled = false
                        binding.genderSpinner.isEnabled = false

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun showDatePickerDialog(view: View) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.dobEt.setText(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}