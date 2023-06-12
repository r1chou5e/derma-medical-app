package com.example.dermamedicalapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.PostRowBinding
import com.squareup.picasso.Picasso

class AdminPostListAdapter:RecyclerView.Adapter<AdminPostListAdapter.PostHolder> {

    private val context: Context
    private val postArrayList: ArrayList<PostModel>
    private lateinit var binding: PostRowBinding
    private lateinit var listener: MyClickListener



    constructor(context: Context, postArrayList: ArrayList<PostModel>, listener: MyClickListener) {
        this.context = context
        this.postArrayList = postArrayList
        this.listener = listener

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
        val imageUrl = model.imageUrl
        val uid = model.uid
        val timestamp = model.timestamp
        val content = model.content

        // set data
        holder.postTitleTv.text = title
        holder.postContentTv.text = content


        if (imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_square) // Placeholder image
                .error(R.drawable.placeholder_square) // Image error
                .fit()
                .centerCrop()
                .into(holder.thumbnailIv)
        } else {
            // if imageUrl empty
            holder.thumbnailIv.setImageResource(R.drawable.placeholder_square)
        }



    }

    interface MyClickListener{
        fun onClick(position: Int)
        {}
    }

    inner class PostHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var postTitleTv:TextView = binding.postTitleTv
        var postContentTv:TextView = binding.postContentTv
        var thumbnailIv:ImageView = binding.thumbnailIv

        init{
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onClick(position)
            }
        }
    }


}