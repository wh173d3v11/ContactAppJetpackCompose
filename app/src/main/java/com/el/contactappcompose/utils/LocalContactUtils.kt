package com.el.contactappcompose.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.el.contactappcompose.domain.Contact

class LocalContactUtils(private val context: Context) {

    var savedLocalContacts = mutableListOf<Contact>()
        private set
        get() {
            if (field.isEmpty()) {
                getAllContacts()
            }
            return field
        }

    fun insertContact(
        contact: Contact
    ): Contact? {
        val contentValues = ContentValues().apply {
            put(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY, contact.firstName)
        }

        val rawContactUri: Uri? =
            context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)

        rawContactUri?.let { rawContactUri ->
            val id = ContentUris.parseId(rawContactUri)
            val nameValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, id)
                put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name);
//                put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.firstName)
//                put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.lastName)
            }
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

            val phoneValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, id)
                put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
                put(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
            }
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

            if (contact.emailAddress.isNotEmpty()) {
                insertEmailInContact(
                    contentResolver = context.contentResolver,
                    id,
                    contact.emailAddress
                )
            }
            val newC = contact.copy(id = ContentUris.parseId(rawContactUri))
            savedLocalContacts.add(newC)
            savedLocalContacts.sortBy { it.firstName }
            return newC
        }
        return null
    }

    private fun insertEmailInContact(
        contentResolver: ContentResolver,
        id: Long,
        emailAddress: String
    ) {
        val emailValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, id)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.Email.ADDRESS, emailAddress)
            put(
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
            )
        }
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
    }

    fun updateContact(
        contact: Contact
    ): Contact {
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
            val result = context.contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                emailValues,
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(
                    contact.id.toString(),
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                )
            )
            if (result == 0) {
                //email not updated, possibly it does not even have email in record.
                //so creating one.
                insertEmailInContact(
                    contentResolver = context.contentResolver,
                    id = contact.id,
                    emailAddress = contact.emailAddress
                )
            }
            Log.d("dinesh", "Email insert done -- $result")
        }

        val origC = savedLocalContacts.indexOfFirst { contact.id == it.id }
        if (origC != -1) {
            savedLocalContacts.removeAt(origC)
            savedLocalContacts.add(origC, contact)
        }
        savedLocalContacts.sortBy { it.firstName }
        return contact
    }


    private fun getAllContacts(): List<Contact> {
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
                    ?: "").toLongOrNull()
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

//    fun getNewContactIntent(contact: Contact): Intent {
//        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
//            type = ContactsContract.RawContacts.CONTENT_TYPE
//            if (contact.emailAddress.isNotEmpty()) {
//                putExtra(ContactsContract.Intents.Insert.EMAIL, contact.emailAddress)
//            }
//            putExtra(ContactsContract.Intents.Insert.PHONE, contact.phoneNumber)
//        }
//        return intent
//    }


    private fun Cursor.getStringOrNull(str: String): String? {
        val col = getColumnIndex(str)
        if (col < 0) return null
        val oStr = getString(col)
        return if (oStr.isNullOrBlank()) return null else oStr
    }
}