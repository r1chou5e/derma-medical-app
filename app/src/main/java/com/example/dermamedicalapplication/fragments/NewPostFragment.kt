package com.example.dermamedicalapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.AdminPostListAdapter
import com.example.dermamedicalapplication.PostModel
import com.example.dermamedicalapplication.PostScreenActivity
import com.example.dermamedicalapplication.R
import com.example.dermamedicalapplication.postContent
import com.example.dermamedicalapplication.postId
import com.example.dermamedicalapplication.postImageUrl
import com.example.dermamedicalapplication.postTimestamp
import com.example.dermamedicalapplication.postTitle
import com.example.dermamedicalapplication.postUid
import com.example.dermamedicalapplication.status


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NewPostFragment : Fragment(), AdminPostListAdapter.MyClickListener {
    private lateinit var postArrayList: ArrayList<PostModel>
    lateinit var myContext: Context
    lateinit var recyclerView: RecyclerView
    lateinit var fm: FragmentManager
    lateinit var viewPage: View



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loadPosts()
        myContext = activity?.applicationContext!!


        viewPage = inflater.inflate(R.layout.fragment_new_post, container, false)





        return viewPage

    }
    private fun loadPosts() {
        postArrayList = ArrayList()

        // get all posts from firebase db
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postArrayList.clear()
                if (snapshot.exists())
                    for (ds in snapshot.children) {
                        // get data as model
                        val model = ds.getValue(PostModel::class.java)
                        // add to arrayList
                        if (model != null) {
                            if (model.status == "pending") {
                                postArrayList.add(model!!)
                            }
                        }
                    }

                if (postArrayList.isNullOrEmpty()) {
                    viewPage.findViewById<RecyclerView>(R.id.newPost).visibility = View.GONE
                    viewPage.findViewById<TextView>(R.id.textView8).visibility = View.VISIBLE
                } else{
                    viewPage.findViewById<RecyclerView>(R.id.newPost).visibility = View.VISIBLE
                    viewPage.findViewById<TextView>(R.id.textView8).visibility = View.GONE
                var postAdapter =
                    context?.let { AdminPostListAdapter(it, postArrayList, this@NewPostFragment) }

                viewPage.findViewById<RecyclerView>(R.id.newPost).adapter = postAdapter
            }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onClick(position: Int) {
        val intent = Intent(this.context, PostScreenActivity::class.java)
        intent.putExtra(postId, postArrayList[position].id)
        intent.putExtra(postImageUrl, postArrayList[position].imageUrl)
        intent.putExtra(postContent, postArrayList[position].content)
        intent.putExtra(postTitle, postArrayList[position].title)
        intent.putExtra(postUid, postArrayList[position].uid)
        intent.putExtra(postTimestamp, postArrayList[position].timestamp.toString())
        intent.putExtra(status,postArrayList[position].status )
        Log.d("haha", postArrayList[position].timestamp.toString())
        if(position != -1)
        {
            startActivity(intent)
        }



    }

}