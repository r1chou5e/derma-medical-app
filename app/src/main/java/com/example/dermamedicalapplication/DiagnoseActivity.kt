package com.example.dermamedicalapplication
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.dermamedicalapplication.ModelDerm.getUniqueApiKey
import com.example.dermamedicalapplication.ModelDerm.modelDerm
import com.example.dermamedicalapplication.ModelDerm.processResponse
import com.example.dermamedicalapplication.QuestionActivity.Companion.imageUri
import com.example.dermamedicalapplication.databinding.ActivityDiagnoseBinding
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.ViewFlipper

class DiagnoseActivity : AppCompatActivity() {

    lateinit var binding: ActivityDiagnoseBinding
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val list: MutableList<String> = mutableListOf()
    private lateinit var viewFlipper: ViewFlipper
    @Suppress("DEPRECATION")
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list.add(QuestionActivity.sex)
        list.add(QuestionActivity.birth)
        list.add(QuestionActivity.pruritus_Pain)
        list.add(QuestionActivity.onset)

        binding.diagnosePic.setImageURI(imageUri)
        viewFlipper = findViewById(R.id.view_flipper)

        // Hiển thị layout phụ
        viewFlipper.displayedChild = 1

        // Xử lý phản hồi sau một khoảng thời gian nhất định
        processImage(imageUri, list)

    }

    private fun processImage(uri: Uri, info: List<String>) {
        executor.execute {
            val serverUrl = "https://t.modelderm.com/api"
            val apiKey = getUniqueApiKey(8)

            val imageList = listOf(uri)
            val result = modelDerm(this, serverUrl, apiKey, imageList, info)

            runOnUiThread {
                val detail = runBlocking { processResponse(result) }
                binding.textView5.text = detail.name
                binding.ProbabilityContent.text = detail.probability
                binding.suggenstionContent.text = detail.suggestion
                binding.DescriptionContent.text = detail.dangerousLevel

                // Hiển thị layout chính
                viewFlipper.displayedChild = 0
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Hủy bỏ handler để tránh rò rỉ bộ nhớ
        handler.removeCallbacksAndMessages(null)
    }

}
