package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.dermamedicalapplication.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postTitleArray: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        loadSearchResult()

    }

    private fun loadSearchResult() {

        postArrayList = ArrayList()
        postTitleArray = ArrayList()


        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postArrayList.clear()
                for (ds in snapshot.children) {
                    // get data as model
                    val post = ds.getValue(PostModel::class.java)
                    // add to arrayList
                    postArrayList.add(post!!)

                    if (post.title.length > 43) {
                        val subString = post.title.substring(0,40)
                        postTitleArray.add("$subString...")
                    }
                    else {
                        postTitleArray.add(post.title)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val postAdapter: ArrayAdapter<String> = ArrayAdapter (
            this, android.R.layout.simple_list_item_1,
            postTitleArray
        )

        binding.searchResultLv.adapter = postAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.searchView.clearFocus()
                if (postTitleArray.contains(p0)) {
                    postAdapter.filter.filter(p0)
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                postAdapter.filter.filter(p0)
                return false
            }

        })

        binding.searchResultLv.setOnItemClickListener { parent, view, position, id ->
            val selectedPost = postArrayList[position]

            val intent = Intent(this@SearchActivity, PostDetailActivity::class.java)
            intent.putExtra("postId", selectedPost.id)
            startActivity(intent)
        }
    }
}