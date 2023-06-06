package com.example.dermamedicalapplication
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.dermamedicalapplication.ModelDerm.getUniqueApiKey
import com.example.dermamedicalapplication.ModelDerm.modelDerm
import com.example.dermamedicalapplication.ModelDerm.processResponse
import com.example.dermamedicalapplication.QuestionActivity.Companion.imageUri
import com.example.dermamedicalapplication.databinding.ActivityDiagnoseBinding
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.ViewFlipper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DiagnoseActivity : AppCompatActivity() {

    lateinit var binding: ActivityDiagnoseBinding
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val list: MutableList<String> = mutableListOf()
    private lateinit var viewFlipper: ViewFlipper
    @Suppress("DEPRECATION")
    private val handler = Handler()
    private lateinit var diagnose: ModelDerm.Disease
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Vui lòng đợi...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

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

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.saveDiagnoseBtn.setOnClickListener {
            saveDiagnose()
        }

        binding.reDiagnoseBtn.setOnClickListener {
            startActivity(Intent(this, QuestionActivity::class.java))
            finish()
        }

    }

    private fun saveDiagnose() {
        // upload post to cloud storage
        Log.d(TAG, "uploadPostToStorage: uploading to storage...")

        // show progress dialog
        progressDialog.setMessage("Đang lưu ảnh...")
        progressDialog.show()

        // timestamp
        val timestamp = System.currentTimeMillis()

        // path of image in firebase storage
        val filePathAndName = "Image/$timestamp.jpg"

        // storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        if (image != null) {
            storageReference.putFile(image!!)
                .addOnSuccessListener {taskSnapShot->
                    Log.d(ContentValues.TAG, "uploadImageToStorage: Image uploaded now getting url...")

                    val uriTask: Task<Uri> = taskSnapShot.storage.downloadUrl
                    while(!uriTask.isSuccessful);

                    val uploadedImageUrl = "${uriTask.result}"

                    uploadDiagnoseToDb(uploadedImageUrl, timestamp)
                }
                .addOnFailureListener {e->
                    Log.d(ContentValues.TAG, "uploadImageToStorage: failed to upload due to ${e.message}")
                    progressDialog.dismiss()
                    Toast.makeText(this, "Không thể upload ảnh vì ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            uploadDiagnoseToDb("", timestamp)
        }
    }

    private fun uploadDiagnoseToDb(uploadedImageUrl: String, timestamp: Long) {
        // upload image
        Log.d(ContentValues.TAG, "uploadDiagnoseToDb: uploading to db")
        progressDialog.setMessage("Đang lưu chẩn đoán...")

        // uid of current user
        val uid = firebaseAuth.uid

        // setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["disease"] = diagnose.name
        hashMap["suggestion"] = diagnose.suggestion
        hashMap["imageUrl"] = uploadedImageUrl
        hashMap["timestamp"] = timestamp
        hashMap["dangerousLevel"] = diagnose.dangerousLevel
        hashMap["probability"] = diagnose.probability

        // db reference DB > Post > PostId > PostInfo
        val ref = FirebaseDatabase.getInstance().getReference("Diagnose")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "uploadDiagnoseToDb: uploaded to DB")
                progressDialog.dismiss()
                Toast.makeText(this, "Đã lưu chẩn đoán!", Toast.LENGTH_SHORT).show()
                image = null
                startActivity(Intent(this, QuestionActivity::class.java))
            }
            .addOnFailureListener {e->
                Log.d(ContentValues.TAG, "uploadPostIntoDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Không thể upload ảnh vì ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

                diagnose = detail
                image = uri

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
