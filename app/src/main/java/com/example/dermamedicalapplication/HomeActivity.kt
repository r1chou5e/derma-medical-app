package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        loadPosts()

        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.diagnose -> {
                    startActivity(Intent(this, QuestionActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun checkUser() {
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            binding.fullnameTv.text = "Chưa đăng nhập"
        }
        else {
            val email = firebaseUser.email

        }
    }

    private fun loadPosts() {
        postArrayList = ArrayList()

        repeat(10) {
            postArrayList.add(PostModel())
        }

        postAdapter = PostAdapter(this@HomeActivity, postArrayList)

        binding.postListRv.adapter = postAdapter

    }
}