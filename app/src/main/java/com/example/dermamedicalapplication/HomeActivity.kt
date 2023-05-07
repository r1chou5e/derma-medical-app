package com.example.dermamedicalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPosts()
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