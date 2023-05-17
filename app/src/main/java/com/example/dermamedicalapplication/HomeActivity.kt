package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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