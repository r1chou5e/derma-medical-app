package com.example.dermamedicalapplication

class UserModel {
    var email:String = ""
    var fullname:String = ""
    var timestamp:Long = 0
    var uid:String = ""
    var profileImage:String = ""
    var userType:String = ""
    var phoneNumber:String = ""

    constructor()

    constructor(uid:String, email:String, fullname:String, timestamp:Long, profileImage:String, userType:String, phoneNumber:String) {
        this.uid = uid
        this.fullname = fullname
        this.timestamp = timestamp
        this.profileImage = profileImage
        this.userType = userType
        this.phoneNumber = phoneNumber
    }

}