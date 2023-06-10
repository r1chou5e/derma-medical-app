package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dermamedicalapplication.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private lateinit var diagnosisArrayList: ArrayList<DiagnoseModel>

    private lateinit var diagnosisAdapter: DiagnosisAdapter

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        loadDiagnosises()

        binding.bottomNavigationView.selectedItemId = R.id.history

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
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
    }

    private fun loadDiagnosises() {

        val uid = firebaseAuth.currentUser?.uid

        diagnosisArrayList = ArrayList()

        if (uid != null) {
            // get all diagnose from firebase db
            val ref = FirebaseDatabase.getInstance().getReference("Diagnose").orderByChild("uid").equalTo(uid)
            ref.addValueEventListener(object : ValueEventListener {
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

                    if (diagnosisArrayList.isEmpty()) {
                        binding.noDiagnoseTv.visibility = View.VISIBLE
                    }
                    else {
                        binding.noDiagnoseTv.visibility = View.GONE

                        // setup adapter
                        diagnosisAdapter = DiagnosisAdapter(this@HistoryActivity, diagnosisArrayList)

                        // set adapter to RecyclerView
                        binding.diagnosisRv.adapter = diagnosisAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}