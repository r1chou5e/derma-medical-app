package com.example.dermamedicalapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.PostRowBinding

class PostAdapter:RecyclerView.Adapter<PostAdapter.PostHolder> {

    private val context: Context
    private val postArrayList: ArrayList<PostModel>
    private lateinit var binding: PostRowBinding

    constructor(context: Context, postArrayList: ArrayList<PostModel>) {
        this.context = context
        this.postArrayList = postArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        binding = PostRowBinding.inflate(LayoutInflater.from(context), parent, false)

        return PostHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        // get data
        val model = postArrayList[position]
        val id = model.id
        val title = model.title
        val uid = model.uid
        val timestamp = model.timestamp
        val content = model.content

        // set data
        holder.postTitleTv.text = title
        holder.postContentTv.text = content

    }

    inner class PostHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var postTitleTv:TextView = binding.postTitleTv
        var postContentTv:TextView = binding.postContentTv
    }



}