package com.example.dermamedicalapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.UserRowBinding

class AdminUserAdapter:RecyclerView.Adapter<AdminUserAdapter.UserHolder> {

    private val context: Context
    private val postArrayList: ArrayList<UserModel>
    private lateinit var binding: UserRowBinding
    private lateinit var listener: MyClickListener

    constructor(context: AdminUserActivity, postArrayList: ArrayList<UserModel>, listener: MyClickListener) {
        this.context = context
        this.postArrayList = postArrayList
        this.listener = listener


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        binding = UserRowBinding.inflate(LayoutInflater.from(context), parent, false)

        return UserHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }



    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        // get data
        val model = postArrayList[position]
        val fullname = model.fullname
        val uid = model.uid
        val timestamp = model.timestamp
        val status = model.status

        // set data
        holder.userId.text = uid
        holder.userName.text = fullname
        holder.userStatus.text = status


        if(binding.userStatus.text.toString() == "locked")
        {
            binding.lock.visibility = View.GONE
            binding.unlock.visibility = View.VISIBLE
        }else if(binding.userStatus.text.toString() == "active")
        {
            binding.lock.visibility = View.VISIBLE
            binding.unlock.visibility = View.GONE
        }


    }

    interface MyClickListener {
        fun deleteUser(position: Int)
        fun lockUser(position: Int)
        fun unlockUser(position: Int)
    }



    inner class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userId: TextView = binding.id
        var userName: TextView = binding.userName
        var userStatus: TextView = binding.userStatus

        init {

            itemView.findViewById<ImageView>(R.id.delete).setOnClickListener() {
                val position = adapterPosition
                listener.deleteUser(position)
            }

            itemView.findViewById<ImageView>(R.id.lock).setOnClickListener() {
                val position = adapterPosition
                listener.lockUser(position)
            }

            itemView.findViewById<ImageView>(R.id.unlock).setOnClickListener() {
                val position = adapterPosition
                listener.unlockUser(position)
            }
        }
    }

        }





