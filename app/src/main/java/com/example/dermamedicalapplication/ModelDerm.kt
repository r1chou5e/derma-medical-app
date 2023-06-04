package com.example.dermamedicalapplication

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.nio.charset.Charset
import kotlin.system.exitProcess

object ModelDerm {
    private val client = OkHttpClient()

    // Phương thức để gửi yêu cầu modelDerm và nhận kết quả trả về
    fun modelDerm(context: Context, url: String, apiKey: String, imageList: List<Uri>,info: List<String>): String {
        val args =
            "<id></id><race></race><birth>" + info[1] + "</birth><sex>" + info[0] + "</sex><location></location><pruritus>"+info[2]+"</pruritus><pain>"+info[2]+"</pain><onset>"+info[3]+"</onset>"

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

    // Phương thức để xử lý và hiển thị kết quả trả về từ modelDerm
    suspend fun processResponse(responseString: String): Disease = coroutineScope {
        val regex = Regex("<([^>]*)>([^<]*)</\\1>")
        val matches = regex.findAll(responseString)
        val result = StringBuilder()
        val disease = Disease("","","","")

        for (match in matches) {
            val tag = match.groupValues[1]
            val value = match.groupValues[2]
            result.append("$tag: $value\n")
            when (tag) {
                "disease" -> {
                    val temp = trimStringToArray(value)
                    try {
                        val detail = async { searchDocument(temp[0]) }
                        val probability = String.format("%.2f", temp[1].toDouble() * 100)
                        disease.probability = "$probability%"
                        disease.name = detail.await().vietnamese + " (" + detail.await().english + ")"
                        disease.suggestion = detail.await().suggestion
                    } catch (e: Exception) {
                        throw e
                    }
                }
                "fine" -> {
                    // Thực hiện hành động khi status là "fine"
                    disease.dangerousLevel = rateLevel(value)
                }
            }
        }

        disease
    }

    data class Disease (
        var name: String,
        var suggestion: String,
        var probability: String,
        var dangerousLevel: String
    )

    data class DiseaseData(
        val english: String,
        val suggestion: String,
        val vietnamese: String
    )

    private fun rateLevel(fine :String) : String {
        val temp = fine.toDouble()
        return when {
            temp > 0.0 && temp < 25.0 -> "Cực kỳ nghiêm trọng"
            temp >= 25.0 && temp < 50.0 -> "Nghiêm trọng"
            temp >= 50.0 && temp < 75.0 -> "Nhẹ"
            else -> {"Bình thường"}
        }
    }

    private suspend fun searchDocument(diseaseName: String): DiseaseData {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("DiseaseInfo")
                .whereEqualTo("English", diseaseName)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                return DiseaseData(
                    english = document.getString("English").toString(),
                    vietnamese = document.getString("Vietnamese").toString(),
                    suggestion = document.getString("Suggestion").toString()
                )
            }
        } catch (exception: Exception) {
            throw exception
        }
        return DiseaseData("", "", "")
    }
}
