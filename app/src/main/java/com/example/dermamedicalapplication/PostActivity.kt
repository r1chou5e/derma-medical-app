package com.example.dermamedicalapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dermamedicalapplication.databinding.ActivityPostBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class PostActivity : AppCompatActivity(), ThumbnailImageViewFragment.ThumbnailImageListener {

    private lateinit var binding: ActivityPostBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri? = null

    private var title = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // init progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Vui lòng đợi...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

        binding.imageBtn.setOnClickListener {
            ThumbnailImageViewFragment().show(supportFragmentManager, "newThumbnail")
        }

        binding.addBtn.setOnClickListener {

            validateData()

        }

    }

    private fun validateData() {
        Log.d(TAG, "validateData: validating data")

        // get data
        title = binding.postTitleEt.text.toString().trim()
        content = binding.postContentEt.text.toString().trim()

        // validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề bài viết.", Toast.LENGTH_SHORT).show()
        }
        else if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung bài viết.", Toast.LENGTH_SHORT).show()
        }
        else {
            uploadPostToStorage()
        }
    }

    private fun uploadPostToStorage() {
        // upload post to cloud storage
        Log.d(TAG, "uploadPostToStorage: uploading to storage...")

        // show progress dialog
        progressDialog.setMessage("Uploading post...")
        progressDialog.show()

        // timestamp
        val timestamp = System.currentTimeMillis()

        // path of image in firebase storage
        val filePathAndName = "Image/$timestamp.jpg"

        // storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        if (imageUri != null) {
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {taskSnapShot->
                    Log.d(TAG, "uploadImageToStorage: Image uploaded now getting url...")

                    val uriTask: Task<Uri> = taskSnapShot.storage.downloadUrl
                    while(!uriTask.isSuccessful);

                    val uploadedImageUrl = "${uriTask.result}"

                    uploadPostToDb(uploadedImageUrl, timestamp)
                }
                .addOnFailureListener {e->
                    Log.d(TAG, "uploadImageToStorage: failed to upload due to ${e.message}")
                    progressDialog.dismiss()
                    Toast.makeText(this, "Không thể upload ảnh vì ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            uploadPostToDb("", timestamp)
        }

    }

    private fun uploadPostToDb(uploadedImageUrl: String, timestamp: Long) {
        // upload image
        Log.d(TAG, "uploadPostIntoDb: uploading to db")
        progressDialog.setMessage("Đang upload ảnh...")

        // uid of current user
        val uid = firebaseAuth.uid

        // setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["content"] = content
        hashMap["imageUrl"] = uploadedImageUrl
        hashMap["timestamp"] = timestamp

        // db reference DB > Post > PostId > PostInfo
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPostIntoDb: uploaded to DB")
                progressDialog.dismiss()
                Toast.makeText(this, "Đã đăng bài viết !", Toast.LENGTH_SHORT).show()
                imageUri = null
                onBackPressed()
            }
            .addOnFailureListener {e->
                Log.d(TAG, "uploadPostIntoDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Không thể upload ảnh vì ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSaveButtonClicked(imageUri: Uri?) {
        this.imageUri = imageUri
    }
}