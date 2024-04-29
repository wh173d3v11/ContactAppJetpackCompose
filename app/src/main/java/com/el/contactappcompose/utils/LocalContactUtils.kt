package com.el.contactappcompose.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.el.contactappcompose.domain.Contact

object LocalContactUtils {

    var savedLocalContacts = listOf<Contact>()
        private set

//    fun insertContact(
//        contentResolver: ContentResolver,
//        firstName: String,
//        lastName: String,
//        email: String
//    ): Uri? {
//        val contentValues = ContentValues().apply {
//            put(
//                ContactsContract.Data.MIMETYPE,
//                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
//            )
//            put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
//            put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
//        }
//
//        val rawContactUri: Uri? =
//            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
//        rawContactUri?.let { rawContactUri ->
//            val id = ContentUris.parseId(rawContactUri)
//            val emailValues = ContentValues().apply {
//                put(ContactsContract.Data.RAW_CONTACT_ID, id)
//                put(
//                    ContactsContract.Data.MIMETYPE,
//                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
//                )
//                put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
//                put(
//                    ContactsContract.CommonDataKinds.Email.TYPE,
//                    ContactsContract.CommonDataKinds.Email.TYPE_HOME
//                )
//            }
//            contentResolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
//        }
//        return rawContactUri
//    }

    fun updateContact(
        context: Context,
        contact: Contact
    ) {
        val contentValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.firstName)
            put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.lastName)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            contentValues,
            "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(
                contact.id.toString(),
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
        )

        val phoneValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            phoneValues,
            "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(
                contact.id.toString(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
        )


        if (contact.emailAddress.isNotEmpty()) {
            val emailValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Email.ADDRESS, contact.emailAddress)
            }
            context.contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                emailValues,
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(
                    contact.id.toString(),
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                )
            )
        }
    }


    fun queryContacts(context: Context): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            "display_name ASC"
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

    fun getNewContactIntent(contact: Contact): Intent {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            if (contact.emailAddress.isNotEmpty()) {
                putExtra(ContactsContract.Intents.Insert.EMAIL, contact.emailAddress)
            }
            putExtra(ContactsContract.Intents.Insert.PHONE, contact.phoneNumber)
        }
        return intent
    }


    fun Cursor.getStringOrNull(str: String): String? {
        val col = getColumnIndex(str)
        if (col < 0) return null
        val oStr = getString(col)
        return if (oStr.isNullOrBlank()) return null else oStr
    }
}