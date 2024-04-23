package com.el.contactappcompose.domain

data class Contact(
    val id:Int,
    val firstName:String,
    val lastName:String,
    val phoneNumber:String,
    val emailAddress: String,
    val profilePictureUrl: String?
){
    val name = "$firstName $lastName"
}