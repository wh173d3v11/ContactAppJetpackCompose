package com.el.contactappcompose.utils

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.el.contactappcompose.domain.Contact

object LocalContactUtils {

    var savedLocalContacts = listOf<Contact>()
        private set

    fun queryContacts(context: Context): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = (c.getStringOrNull(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    ?: "").toIntOrNull()
                    ?: return@use
                val fullName =
                    c.getStringOrNull(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) ?: ""
                val phoneNumber =
                    c.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER) ?: ""
                val (firstName, lastName) = run {
                    val nameParts = fullName.split(" ")
                    when {
                        nameParts.size >= 2 -> Pair(nameParts[0], nameParts[1])
                        else -> Pair(fullName, "")
                    }
                }

                Log.d(
                    "LocalContactUtils",
                    "id = $id,\n" +
                            "firstName = $firstName,\n" +
                            "lastName = $lastName,\n" +
                            "phoneNumber = $phoneNumber"
                )

                contactsList.add(
                    Contact(
                        id = id,
                        firstName = firstName,
                        lastName = lastName,
                        profilePictureUrl = "",
                        emailAddress = "",
                        phoneNumber = phoneNumber,
                        isRemote = false
                    )
                )
            }
        }
        savedLocalContacts = contactsList
        return contactsList
    }

    fun Cursor.getStringOrNull(str: String): String? {
        val col = getColumnIndex(str)
        if (col < 0) return null
        val oStr = getString(col)
        return if (oStr.isNullOrBlank()) return null else oStr
    }
}