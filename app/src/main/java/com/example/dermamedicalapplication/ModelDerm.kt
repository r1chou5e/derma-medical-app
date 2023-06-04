package com.example.dermamedicalapplication

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object ModelDerm {
    private val client = OkHttpClient()

    // Phương thức để gửi yêu cầu modelDerm và nhận kết quả trả về
    fun modelDerm(context: Context, url: String, apiKey: String, imageList: List<Uri>): String {
        val args =
            "<id></id><race></race><birth></birth><sex></sex><location></location><pruritus></pruritus><pain></pain><onset></onset>"

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("args1", apiKey)
            .addFormDataPart("args2", args)

        for ((index, imageUri) in imageList.withIndex()) {
            if (index >= 5) {
                println("WARNING IGNORE: $imageUri")
                continue
            }

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "image_$index.jpg"

            inputStream?.use { stream ->
                builder.addFormDataPart(
                    "file",
                    fileName,
                    stream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                )
            }
        }

        val requestBody = builder.build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                exitProcess(1)
            } else {
                val responseBody = response.body.bytes()

                val charset = Charset.forName("UTF-8") // Thay đổi encoding tùy thuộc vào encoding của dữ liệu trả về

                return responseBody.toString(charset)
            }
        }
    }

    // Phương thức để xử lý và hiển thị kết quả trả về từ modelDerm
    fun processResponse(responseString: String): Disease {
        val regex = Regex("<([^>]*)>([^<]*)</\\1>")
        val matches = regex.findAll(responseString)
        val result = StringBuilder()
        val disease = Disease()

        for (match in matches) {
            val tag = match.groupValues[1]
            val value = match.groupValues[2]
            result.append("$tag: $value\n")
            when (tag) {
                "disease" -> {
                    // Thực hiện hành động khi status là "disease"
                    val temp = trimStringToArray(value)
                    val detail = searchInJsonFile(temp[0])
                    disease.probability = temp[1].toDouble().times(100).toString() + "%"
                    disease.name = detail.vietnamese + " (" + detail.english + ")"
                    disease.suggestion = detail.suggestion
                }
                "fine" -> {
                    // Thực hiện hành động khi status là "fine"
                    disease.dangerousLevel = value

                }
            }

        }
        return disease
    }

    class Disease {
        var name: String = ""
        var suggestion: String = ""
        var probability: String = ""
        var dangerousLevel: String = ""
    }

    class DiseaseDetail {
        var english: String = ""
        var suggestion: String = ""
        var vietnamese: String = ""
    }

    // Phương thức để tạo một API key duy nhất với độ dài xác định
    fun getUniqueApiKey(length: Int): String {
        val lettersAndDigits = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { lettersAndDigits.random() }
            .joinToString("")
    }

    private fun trimStringToArray(input: String): Array<String> {
        return input.split(",")
            .map { it.trim() }
            .toTypedArray()
    }

    private fun searchInJsonFile(value: String): DiseaseDetail {
        val diseaseDetail = DiseaseDetail()
        val key = "English"
        val jsonFilePath = "app/database/diseases_unlabeled.json"
        val jsonData = String(Files.readAllBytes(Paths.get(jsonFilePath)), Charset.defaultCharset())
        val jsonArray = JSONArray(jsonData)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.optString(key) == value) {
                with(diseaseDetail) {
                    english = jsonObject.optString("English")
                    vietnamese = jsonObject.optString("Vietnamese")
                    suggestion = jsonObject.optString("Suggestion")
                }
                break
            }
        }
        return diseaseDetail
    }

    private fun searchDocument(disease: String): DiseaseDetail {
        val db = FirebaseFirestore.getInstance()
        val diseaseDetail = DiseaseDetail()
        db.collection("/DiseaseInfo")
            .whereEqualTo("English", disease)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentSnapshot = documents.documents[0]
                    with(diseaseDetail) {
                        english = documentSnapshot.getString("English").toString()
                        vietnamese = documentSnapshot.getString("Vietnamese").toString()
                        suggestion = documentSnapshot.getString("Suggestion").toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: ${exception.message}")
            }
        return diseaseDetail
    }

}
