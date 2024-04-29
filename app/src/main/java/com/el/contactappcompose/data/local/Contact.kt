package com.el.contactappcompose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val profilePictureUrl: String
)