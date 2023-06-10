package com.example.dermamedicalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermamedicalapplication.databinding.ActivityDiagnoseDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class DiagnoseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnoseDetailBinding
    private var diagnoseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get diagnoseId
        diagnoseId = intent.getStringExtra("diagnoseId")!!
        loadData()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.diagnoseBtn.setOnClickListener {
            startActivity(Intent(this, QuestionActivity::class.java))
            finish()
        }

    }

    private fun loadData() {
        val ref = FirebaseDatabase.getInstance().getReference("Diagnose").child(diagnoseId)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val diagnose = snapshot.getValue(DiagnoseModel::class.java)
                    val imageUrl = diagnose?.imageUrl.toString()
                    val timestamp = diagnose?.timestamp?.toLong()

                    binding.timestampValueTv.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(timestamp?.let { Date(it) }).toString()
                    binding.diseaseNameTv.text = diagnose?.disease.toString()
                    binding.probabilityValueTv.text = diagnose?.probability.toString()
                    binding.dangerousLevelContentTv.text = diagnose?.dangerousLevel.toString()
                    binding.suggenstionContentTv.text = diagnose?.suggestion.toString()


                    if (imageUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_square) // Placeholder image
                            .error(R.drawable.placeholder_square) // Image error
                            .fit()
                            .centerCrop()
                            .into(binding.diseasePic)
                    } else {
                        // if imageUrl empty
                        binding.diseasePic.setImageResource(R.drawable.placeholder_square)
                    }

                }
                else {

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}