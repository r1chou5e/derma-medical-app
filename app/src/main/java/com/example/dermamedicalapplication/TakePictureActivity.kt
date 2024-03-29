package com.example.dermamedicalapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.dermamedicalapplication.databinding.ActivityTakePictureBinding

class TakePictureActivity : AppCompatActivity() {
    private var imageUri:Uri = QuestionActivity.imageUri
    private lateinit var binding: ActivityTakePictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTakePictureBinding.inflate(layoutInflater)
        binding.displayPic.setImageURI(imageUri)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        binding.Confirm.setOnClickListener{
            startActivity(Intent(this, DiagnoseActivity::class.java))
        }

        binding.Cancel.setOnClickListener{
            startActivity(Intent(this, QuestionActivity::class.java))
        }

        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }
}



