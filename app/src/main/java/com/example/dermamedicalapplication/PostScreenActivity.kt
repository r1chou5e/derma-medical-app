package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dermamedicalapplication.databinding.ActivityAdminPostBinding

import com.example.dermamedicalapplication.databinding.ActivityPostScreenBinding
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.lang.Integer.parseInt

const val postId = "id"
const val postImageUrl = "imageUrl"
const val postTitle = "title"
const val postContent = "content"

class PostScreenActivity : AppCompatActivity() {


    lateinit var binding: ActivityPostScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postTitle.text = intent.getStringExtra(postTitle)
        binding.postContent.text = intent.getStringExtra(postContent)


        if (intent.getStringExtra(postImageUrl)?.isNotEmpty() == true) {
            Picasso.get()
                .load(intent.getStringExtra(postImageUrl))
                .placeholder(R.drawable.placeholder_square) // Placeholder image
                .error(R.drawable.placeholder_square) // Image error
                .fit()
                .centerCrop()
                .into(binding.postPic)
        } else {
            // if imageUrl empty
            binding.postPic.setImageResource(R.drawable.placeholder_square)
        }


        binding.approvedBtn.setOnClickListener{
            updateApprovedPost()
        }

        binding.deleteBtn.setOnClickListener{
            updateDeletePost()
        }


    }

    private fun updateApprovedPost() {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "approved"
        intent.getStringExtra(postId)?.let { ref.child(it).updateChildren(updateStatus) }
        startActivity(Intent(this, ActivityAdminPostBinding::class.java))
    }

    private fun updateDeletePost() {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "deleted"
        intent.getStringExtra(postId)?.let { ref.child(it).updateChildren(updateStatus) }
        startActivity(Intent(this, ActivityAdminPostBinding::class.java))
    }
}