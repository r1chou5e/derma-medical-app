package com.example.dermamedicalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dermamedicalapplication.databinding.ActivityUserPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPostBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        loadPosts()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadPosts() {
        val uid = firebaseAuth.currentUser?.uid

        postArrayList = ArrayList()

        if (uid != null) {
            // get all posts from firebase db
            val ref = FirebaseDatabase.getInstance().getReference("Post").orderByChild("uid").equalTo(uid)

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
                        postAdapter = PostAdapter(this@UserPostActivity, postArrayList)

                        // set adapter to RecyclerView
                        binding.postListRv.adapter = postAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }


    }
}