package com.el.contactappcompose.data

import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.remote.ResultUsers
import com.el.contactappcompose.domain.Contact

fun ResultUsers.toContactEntity(): ContactEntity {
    return ContactEntity(
        id = id.hashCode().toLong(),
        firstName = name.first,
        lastName = name.last,
        profilePictureUrl = picture.medium,
        phoneNumber = phone,
        emailAddress = email
    )
}
fun Contact.toContactEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        profilePictureUrl = profilePictureUrl ?: "",
        phoneNumber = phoneNumber,
        emailAddress = emailAddress
    )
}

fun ContactEntity.toContact(): Contact {
    return Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        profilePictureUrl = profilePictureUrl,
        phoneNumber = phoneNumber,
        emailAddress = emailAddress
    )
}