package com.example.dermamedicalapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.example.dermamedicalapplication.ModelDerm.getUniqueApiKey
import com.example.dermamedicalapplication.ModelDerm.modelDerm
import com.example.dermamedicalapplication.ModelDerm.processResponse
import com.example.dermamedicalapplication.QuestionActivity.Companion.imageUri
import com.example.dermamedicalapplication.databinding.ActivityDiagnoseBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DiagnoseActivity : AppCompatActivity() {

    lateinit var binding: ActivityDiagnoseBinding
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.diagnosePic.setImageURI(imageUri)

        processImage(imageUri)

    }

    private fun processImage(uri: Uri) {
        executor.execute {
            val serverUrl = "https://t.modelderm.com/api"
            val apiKey = getUniqueApiKey(8)

            val imageList = listOf(uri)
            val result = modelDerm(this, serverUrl, apiKey, imageList)

            runOnUiThread {
                // Update UI
                val detail = processResponse(result)
                binding.textView5.text = detail.name
                binding.ProbabilityContent.text = detail.probability
                binding.suggenstionContent.text = detail.suggestion
            }
        }
    }
}
