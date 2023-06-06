package com.example.dermamedicalapplication

import java.sql.Timestamp

class DiagnoseModel {
    var id:String = ""
    var disease:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    var imageUrl:String = ""
    var suggestion:String = ""
    var probability:String = ""
    var dangerousLevel:String = ""

    constructor()

    constructor(id:String, disease:String, timestamp: Long, uid:String, imageUrl:String, suggestion:String, dangerousLevel:String, probability: String) {
        this.id = id
        this.disease = disease
        this.timestamp = timestamp
        this.uid = uid
        this.imageUrl = imageUrl
        this.suggestion = suggestion
        this.probability = probability
        this.dangerousLevel = dangerousLevel
    }

}