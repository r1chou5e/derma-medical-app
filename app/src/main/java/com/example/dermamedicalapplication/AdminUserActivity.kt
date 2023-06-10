package com.example.dermamedicalapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.ActivityAdminPostBinding
import com.example.dermamedicalapplication.databinding.ActivityAdminUserBinding
import com.google.firebase.auth.FirebaseAuth
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
    val firebaseAuth = FirebaseAuth.getInstance()

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

        binding.backBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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
         val builder = AlertDialog.Builder(this@AdminUserActivity)
         builder.setMessage("Bạn có chắc muốn xóa người dùng này ?")
             .setCancelable(false)
             .setPositiveButton("Có") { dialog, id ->
                 // Delete selected note from database
                 val ref = FirebaseDatabase.getInstance().getReference("User")
                 Log.d("haha", position.toString())
                 if(position != -1) {
                     ref.child(userArrayList[position].uid).removeValue()
                 }
                 Toast.makeText(this, "Người dùng đã được xoá.", Toast.LENGTH_SHORT).show()

             }
             .setNegativeButton("Không") { dialog, id ->
                 // Dismiss the dialog
                 dialog.dismiss()
             }
         val alert = builder.create()
         alert.show()

    }

     override fun lockUser(position: Int) {
         val ref = FirebaseDatabase.getInstance().getReference("User")
         val updateStatus: HashMap<String, Any> = HashMap()
         updateStatus["status"] = "locked"
         if(position != -1) {
             ref.child(userArrayList[position].uid).updateChildren(updateStatus)
         }
         Toast.makeText(this, "Người dùng đã bị khoá.", Toast.LENGTH_SHORT).show()
    }

    override fun unlockUser(position: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("User")
        val updateStatus: HashMap<String, Any> = HashMap()
        updateStatus["status"] = "active"
        if(position != -1) {
            ref.child(userArrayList[position].uid).updateChildren(updateStatus)
        }
        Toast.makeText(this, "Người dùng đã được mở khoá. ", Toast.LENGTH_SHORT).show()
    }
}


