package com.example.dermamedicalapplication

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.Toast
import com.example.dermamedicalapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Vui lòng đợi...")
        progressDialog.setCanceledOnTouchOutside(false)

        // handle back click
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener {
            validateData()
        }
    }

    private var fullname = ""
    private var email = ""
    private var password = ""
    private var phoneNumber = ""

    private fun validateData() {
        // Input data
        fullname = binding.fullnameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        phoneNumber = binding.phoneEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        // Validate data
        if (fullname.isEmpty()) {
            // empty name
            Toast.makeText(this, "Họ và tên của bạn không được để trống...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email pattern
            Toast.makeText(this, "Định dạng email của bạn chưa đúng...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Mật khẩu không được để trống...", Toast.LENGTH_SHORT).show()
        }
        else if (cPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu...", Toast.LENGTH_SHORT).show()
        }
        else if (password != cPassword) {
            Toast.makeText(this, "Mật khẩu không khớp...", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        // Create account with Firebase Auth

        // show progress
        progressDialog.setMessage("Tài khoản đang được tạo...")
        progressDialog.show()

        // create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // account created, add user info into db
                updateUserInfo()
            }
            .addOnFailureListener {e ->
                // failed creating account
                progressDialog.dismiss()
                Toast.makeText(this, "Tạo tài khoản thất bại !!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        // Save user info in Firebase realtime db
        progressDialog.setMessage("Đang lưu thông tin người dùng...")

        // timestamp
        val timestamp = System.currentTimeMillis()
        // user id
        val uid = firebaseAuth.uid

        // Setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["fullname"] = fullname
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        // Set data to db
        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // user info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Tài khoản được tạo thành công !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {e ->
                // failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Lưu thông tin người dùng thất bại !!", Toast.LENGTH_SHORT).show()
            }
    }
}