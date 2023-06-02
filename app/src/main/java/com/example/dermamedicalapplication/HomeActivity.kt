package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dermamedicalapplication.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        binding.addPostBtn.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

        binding.drinkWaterRl.setOnClickListener {
            startActivity(Intent(this, WaterReminderActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
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

            val userRef = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val fullname = snapshot.child("fullname").value.toString()
                        binding.fullnameTv.text = fullname

                        binding.welcomeTv.text = "Xin chào, " + fullname.substringAfterLast(" ") + " !"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi có lỗi xảy ra
                }
            })

        }
    }

    private fun loadPosts() {
        postArrayList = ArrayList()

        // get all posts from firebase db
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postArrayList.clear()
                for (ds in snapshot.children) {
                    // get data as model
                    val model = ds.getValue(PostModel::class.java)
                    // add to arrayList
                    postArrayList.add(model!!)
                }

                if (postArrayList.isEmpty()) {
                    binding.noPostTv.visibility = View.VISIBLE
                }
                else {
                    binding.noPostTv.visibility = View.GONE

                    // setup adapter
                    postAdapter = PostAdapter(this@HomeActivity, postArrayList)

                    // set adapter to RecyclerView
                    binding.postListRv.adapter = postAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}