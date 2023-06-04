package com.example.dermamedicalapplication

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DiseaseDetail {
    var english: String = ""
    var suggestion: String = ""
    var vietnamese: String = ""
}

private fun searchDocument(diseaseName: String): DiseaseDetail {
    val db = Firebase.firestore
    val diseaseDetail = DiseaseDetail

    db.collection("DiseaseInfo")
        .whereEqualTo("English", diseaseName)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                with(diseaseDetail) {
                    english = document.getString("English").toString()
                    vietnamese = document.getString("Vietnamese").toString()
                    suggestion = document.getString("Suggestion").toString()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents: ", exception)
        }
    return diseaseDetail
}

fun main () {
    val diseaseDetail = searchDocument("Skin tag")
    println(diseaseDetail.english)
    println(diseaseDetail.vietnamese)
    println(diseaseDetail.suggestion)
}