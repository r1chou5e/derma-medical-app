package com.example.dermamedicalapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dermamedicalapplication.databinding.ActivityAdminPostBinding
import com.example.dermamedicalapplication.databinding.ActivityPostScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

const val postId = "id"
const val postImageUrl = "imageUrl"
const val postTitle = "title"
const val postContent = "content"
const val uid = "uid"
const val timestamp = "timestamp"
const val status = "status"

class PostScreenActivity : AppCompatActivity() {


    lateinit var binding: ActivityPostScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()

        if (intent.getStringExtra(status) == "approved")
        {
            binding.approvedBtn.visibility = View.GONE
            binding.denyBtn.visibility = View.GONE
            binding.undoDenyBtn.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
        }else if (intent.getStringExtra(status) == "pending")
        {
            binding.approvedBtn.visibility = View.VISIBLE
            binding.denyBtn.visibility = View.VISIBLE
            binding.undoDenyBtn.visibility = View.GONE
            binding.deleteBtn.visibility = View.GONE
        }else
        {
            binding.approvedBtn.visibility = View.GONE
            binding.denyBtn.visibility = View.GONE
            binding.undoDenyBtn.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.VISIBLE
        }
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        binding.approvedBtn.setOnClickListener{
            updateApprovedPost()
        }

        binding.denyBtn.setOnClickListener{
            updateDenyPost()
        }

        binding.undoDenyBtn.setOnClickListener{
            updateUndoPost()
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
        intent.getStringExtra(postId)?.let { ref.child(it).removeValue() }
        startActivity(Intent(this, ActivityAdminPostBinding::class.java))
    }

    private fun updateUndoPost() {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "pending"
        intent.getStringExtra(postId)?.let { ref.child(it).updateChildren(updateStatus) }
        startActivity(Intent(this, ActivityAdminPostBinding::class.java))
    }

    private fun updateDenyPost() {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "denied"
        intent.getStringExtra(postId)?.let { ref.child(it).updateChildren(updateStatus) }
        startActivity(Intent(this, ActivityAdminPostBinding::class.java))
    }

    private fun loadData() {

        val ref = FirebaseDatabase.getInstance().getReference("Post").child(postId)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(PostModel::class.java)
                    val imageUrl = intent.getStringExtra(postImageUrl)
                    val timestamp = intent.getStringExtra(timestamp)

                    binding.timestampTv.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(timestamp?.let { Date(it) }).toString()
                    binding.postTitleTv.text = intent.getStringExtra(postTitle)
                    binding.postContentTv.text = intent.getStringExtra(postContent)

                    val uid = post?.uid.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("User").child(uid)

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(UserModel::class.java)
                                val fullname = user?.fullname.toString()
                                binding.authorTv.text = "Tác giả: $fullname"
                            } else {
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })


                    if (imageUrl != null) {
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

                }
                else {

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}