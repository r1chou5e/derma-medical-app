package com.example.dermamedicalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityPostDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    private var postId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get postId to view it
        postId = intent.getStringExtra("postId")!!

        loadData()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadData() {

        val ref = FirebaseDatabase.getInstance().getReference("Post").child(postId)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(PostModel::class.java)
                    val imageUrl = post?.imageUrl.toString()
                    val timestamp = post?.timestamp?.toLong()

                    binding.timestampTv.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(timestamp?.let { Date(it) }).toString()
                    binding.postTitleTv.text = post?.title.toString()
                    binding.postContentTv.text = post?.content.toString()

                    if (imageUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_square) // Placeholder image
                            .error(R.drawable.placeholder_square) // Image error
                            .fit()
                            .centerCrop()
                            .into(binding.thumbnailIv)
                    } else {
                        // if imageUrl empty
                        binding.thumbnailIv.setImageResource(R.drawable.placeholder_square)
                    }

                }
                else {

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}