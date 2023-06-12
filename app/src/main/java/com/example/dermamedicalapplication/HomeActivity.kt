package com.example.dermamedicalapplication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.dermamedicalapplication.WaterReminderActivity.Companion.currentProgress
import com.example.dermamedicalapplication.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Date

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var postArrayList: ArrayList<PostModel>

    private lateinit var postAdapter: PostAdapter

    private var dangerousLevel = ""

    private var lastDiagnoseId = ""

    lateinit var sharedPref: SharedPreferences
    //now get Editor
    lateinit var  editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Create object of SharedPreferences.

        sharedPref  = getSharedPreferences("Water", MODE_PRIVATE);
        editor = sharedPref.edit();
        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        loadPosts()

        loadDiagnoseNumber()

        loadCurrentStatus()

        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.diagnose -> {
                    startActivity(Intent(this, QuestionActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        binding.addPostBtn.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

        binding.drinkWaterRl.setOnClickListener {
            startActivity(Intent(this, WaterReminderActivity::class.java))

        }

        binding.searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.exploreBtn.setOnClickListener {
            startActivity(Intent(this, IntroduceActivity::class.java))
        }

        binding.statusBarRl.setOnClickListener {
            val intent = Intent(this, DiagnoseDetailActivity::class.java)
            intent.putExtra("diagnoseId", lastDiagnoseId)
            startActivity(intent)
        }

        Log.d("haha", currentProgress.toString())
        var water = currentProgress
        binding.numberDrinkWaterTv.text = "${water}/2000"


    }

    private fun loadCurrentStatus() {

        var diagnosisArrayList = ArrayList<DiagnoseModel>()

        val uid = firebaseAuth.currentUser?.uid

        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Diagnose").orderByChild("uid").equalTo(uid)
            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diagnosisArrayList.clear()
                    for (ds in snapshot.children) {
                        // get data as model
                        val model = ds.getValue(DiagnoseModel::class.java)
                        // add to arrayList
                        if (model != null) {
                            diagnosisArrayList.add(model)
                        }
                    }

                    diagnosisArrayList = diagnosisArrayList.sortedByDescending { it.timestamp }.toMutableList() as ArrayList<DiagnoseModel>

                    var icon: ImageView = binding.dermaStatusIconIv

                    if (diagnosisArrayList.size > 0) {
                        lastDiagnoseId = diagnosisArrayList[0].id

                        binding.dermaStatusTimeTv.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                            .format(diagnosisArrayList[0].timestamp?.let { Date(it) }).toString()

                        dangerousLevel = diagnosisArrayList[0].dangerousLevel

                        if (dangerousLevel == "Cực kỳ nghiêm trọng") {
                            icon.setImageResource(R.drawable.confused)
                        }
                        else if (dangerousLevel == "Nghiêm trọng") {
                            icon.setImageResource(R.drawable.sad)
                        }
                        else if (dangerousLevel == "Nhẹ") {
                            icon.setImageResource(R.drawable.neutral)
                        }
                        else if (dangerousLevel == "Bình thường") {
                            icon.setImageResource(R.drawable.smiling)
                        }
                    }
                    else {
                        icon.setImageResource(R.drawable.smiling)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }
    }

    private fun loadDiagnoseNumber() {
        val uid = firebaseAuth.currentUser?.uid

        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Diagnose").orderByChild("uid").equalTo(uid)
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.numberDiagnosisTv.text = snapshot.childrenCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun checkUser() {
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            binding.fullnameTv.text = "Chưa đăng nhập"
        }
        else {
            val email = firebaseUser.email

            val userRef = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val fullname = snapshot.child("fullname").value.toString()
                        binding.fullnameTv.text = fullname

                        binding.welcomeTv.text = "Xin chào, " + fullname.substringAfterLast(" ") + " !"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi có lỗi xảy ra
                }
            })

        }
    }

    private fun loadPosts() {
        postArrayList = ArrayList()

        // get all posts from firebase db
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postArrayList.clear()
                for (ds in snapshot.children) {
                    // get data as model
                    val model = ds.getValue(PostModel::class.java)
                    // add to arrayList
                    if (model != null) {
                        if(model.status == "approved")
                            postArrayList.add(model!!)
                    }
                }

                if (postArrayList.isEmpty()) {
                    binding.noPostTv.visibility = View.VISIBLE
                }
                else {
                    binding.noPostTv.visibility = View.GONE

                    // setup adapter
                    postAdapter = PostAdapter(this@HomeActivity, postArrayList)

                    // set adapter to RecyclerView
                    binding.postListRv.adapter = postAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}