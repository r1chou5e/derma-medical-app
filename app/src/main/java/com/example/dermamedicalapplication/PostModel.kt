package com.example.dermamedicalapplication

import java.sql.Timestamp

class PostModel {
    var id:String = ""
    var title:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    var imageUrl:String = ""
    var content:String = ""
    var status:String = ""

    constructor()

    constructor(id:String, title:String, timestamp: Long, uid:String, imageUrl:String, content:String, status:String) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.uid = uid
        this.imageUrl = imageUrl
        this.content = content
        this.status = status
    }

}