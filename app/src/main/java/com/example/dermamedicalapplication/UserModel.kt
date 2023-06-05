package com.example.dermamedicalapplication

import java.sql.Timestamp

class UserModel {

    var fullname:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    var status:String = ""

    constructor()

    constructor(id:String, fullname:String, uid:String, status:String) {
        this.fullname = fullname
        this.uid = uid
        this.status = status
    }

}