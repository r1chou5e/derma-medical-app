package com.example.dermamedicalapplication

import java.sql.Timestamp

class PostModel {
    var id:String = ""
    var title:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    var image:String = ""
    var content:String = ""

    constructor()

    constructor(id:String, title:String, timestamp: Long, uid:String, image:String, content:String) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.uid = uid
        this.image = image
        this.content = content
    }

}