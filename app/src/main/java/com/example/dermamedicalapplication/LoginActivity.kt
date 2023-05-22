package com.example.dermamedicalapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.dermamedicalapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Vui lòng đợi...")
        progressDialog.setCanceledOnTouchOutside(false)

        // handle login click
        binding.loginBtn.setOnClickListener {

            validateData()

        }

        // handle register click
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        // Input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // Validate data
        if (email.isEmpty()) {
            Toast.makeText(this, "Email không được để trống...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email của bạn chưa đúng...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Mật khẩu không được để trống...", Toast.LENGTH_SHORT).show()
        }
        else {
            loginUser()
        }
    }

    private fun loginUser() {

        // show progress dialog
        progressDialog.setMessage("Đang tiến hành đăng nhập...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Đăng nhập thất bại !", Toast.LENGTH_SHORT).show()
            }

    }

    private fun checkUser() {

        progressDialog.setMessage("Đang kiểm tra người dùng...")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    // get user type
                    val userType = snapshot.child("userType").value

                    if (userType == "user") {
                        // start Home
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()

                    } else if (userType == "admin") {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}