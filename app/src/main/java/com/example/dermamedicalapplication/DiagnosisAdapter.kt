package com.example.dermamedicalapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.DiagnosisRowBinding

class DiagnosisAdapter(
    private val context: Context,
    private val diagnosisArrayList: ArrayList<DiagnosisModel>
) : RecyclerView.Adapter<DiagnosisAdapter.DiagnosisHolder>() {

    private lateinit var binding: DiagnosisRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisHolder {
        binding = DiagnosisRowBinding.inflate(LayoutInflater.from(context), parent, false)

        return DiagnosisHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return diagnosisArrayList.size
    }

    override fun onBindViewHolder(holder: DiagnosisHolder, position: Int) {

    }

    inner class DiagnosisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }



}