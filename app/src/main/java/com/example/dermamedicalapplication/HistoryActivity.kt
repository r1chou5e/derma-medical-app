package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private lateinit var diagnosisArrayList: ArrayList<DiagnosisModel>

    private lateinit var diagnosisAdapter: DiagnosisAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        diagnosisArrayList = ArrayList()

        repeat(10) {
            diagnosisArrayList.add(DiagnosisModel())
        }

        diagnosisAdapter = DiagnosisAdapter(this@HistoryActivity, diagnosisArrayList)

        binding.diagnosisRv.adapter = diagnosisAdapter

    }
}