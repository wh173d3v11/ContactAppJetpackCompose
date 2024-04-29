package com.el.contactappcompose.domain

data class Contact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val profilePictureUrl: String?,
    var isRemote: Boolean = true,
) {
    val name = "$firstName $lastName"
    val labelName
        get() = if (isRemote) "from Remote" else "from Local"
}