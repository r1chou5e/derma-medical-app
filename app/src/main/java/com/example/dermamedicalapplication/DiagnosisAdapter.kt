package com.example.dermamedicalapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermamedicalapplication.databinding.DiagnosisRowBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class DiagnosisAdapter:RecyclerView.Adapter<DiagnosisAdapter.DiagnoseHolder> {
    private val context: Context
    private val diagnosisArrayList: ArrayList<DiagnoseModel>
    private lateinit var binding: DiagnosisRowBinding

    constructor(context: Context, diagnosisArrayList: ArrayList<DiagnoseModel>) {
        this.context = context
        this.diagnosisArrayList = diagnosisArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnoseHolder {
        binding = DiagnosisRowBinding.inflate(LayoutInflater.from(context), parent, false)

        return DiagnoseHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return diagnosisArrayList.size
    }

    override fun onBindViewHolder(holder: DiagnoseHolder, position: Int) {

        // get data
        val model = diagnosisArrayList[position]
        val id = model.id
        val disease = model.disease
        val imageUrl = model.imageUrl
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            .format(model.timestamp?.let { Date(it) }).toString()
        val probability = model.probability
        val dangerousLevel = model.dangerousLevel

        // set data
        holder.diseaseTv.text = getViName(disease)
        holder.timestampTv.text = "Thời gian: $timestamp"
        holder.probabilityTv.text = "Tỷ lệ chính xác: $probability"
        holder.statusTv.text = "Tình trạng: $dangerousLevel"

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

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, PostDetailActivity::class.java)
//            intent.putExtra("postId", id)
//            context.startActivity(intent)
        }
    }

    private fun getViName(disease: String): String {
        val viName = disease.substringBefore(" (")
        return viName
    }

    inner class DiagnoseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timestampTv:TextView = binding.timestampTv
        var diseaseTv:TextView = binding.diseaseTv
        var probabilityTv:TextView = binding.probabilityTv
        var thumbnailIv:ImageView = binding.thumbnailIv
        var statusTv:TextView = binding.statusTv
    }

}