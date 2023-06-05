package com.example.dermamedicalapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.ActivityAdminPostBinding
import com.example.dermamedicalapplication.databinding.ActivityAdminUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

const val userId = "userId"

class AdminUserActivity : AppCompatActivity(), AdminUserAdapter.MyClickListener {
    private lateinit var userArrayList: ArrayList<UserModel>
    private lateinit var binding: ActivityAdminUserBinding
    lateinit var myContext: Context
    lateinit var recyclerView: RecyclerView
    lateinit var fm: FragmentManager
    lateinit var viewPage: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUsers()
        binding.adminNav.selectedItemId = R.id.user

        binding.adminNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.post -> {
                    startActivity(Intent(this, AdminPostActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }


    }

    private fun loadUsers() {
        userArrayList = ArrayList()
        // get all posts from firebase db
        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear()
                for (ds in snapshot.children) {
                    // get data as model
                    val model = ds.getValue(UserModel::class.java)
                    // add to arrayList
                    if (model != null) {
                        userArrayList.add(model!!)
                        Log.d("hahah", userArrayList.toString())
                    }

                    var userAdapter = AdminUserAdapter(
                        this@AdminUserActivity,
                        userArrayList,
                        this@AdminUserActivity
                    )


                    binding.userListRv.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

     override fun deleteUser(position: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("User")
         Log.d("haha", position.toString())
         if(position != -1) {
             ref.child(userArrayList[position].uid).removeValue()
         }

    }

     override fun lockUser(position: Int) {
         val ref = FirebaseDatabase.getInstance().getReference("User")
         val updateStatus: HashMap<String, Any> = HashMap()
         updateStatus["status"] = "locked"
         if(position != -1) {
             ref.child(userArrayList[position].uid).updateChildren(updateStatus)
         }
    }

    override fun unlockUser(position: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("User")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "active"
        if(position != -1) {
            ref.child(userArrayList[position].uid).updateChildren(updateStatus)
        }
    }
}


