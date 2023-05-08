package com.example.dermamedicalapplication

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

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        loadDiagnosises()
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