package com.el.contactappcompose.vm

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.el.contactappcompose.R
import com.el.contactappcompose.TAG
import com.el.contactappcompose.data.local.ContactDatabase
import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.repo.LocalContactRepo
import com.el.contactappcompose.data.toContact
import com.el.contactappcompose.data.toContactEntity
import com.el.contactappcompose.domain.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    pager: Pager<Int, ContactEntity>,
    val contactDb: ContactDatabase,
    val localContactsRepo: LocalContactRepo
) : ViewModel() {

    //remote data
    val contactPagingFlow = pager
        .flow
        .map { data ->
            data.map { it.toContact() }
        }
        .cachedIn(viewModelScope)

    //local data
    val localContactList: List<Contact>
        get() = localContactsRepo.savedLocalContacts

    //for detail screen, edit/create
    var selectedContact: Contact? = null
        set(value) {
            field = value
            firstName = value?.firstName ?: ""
            lastName = value?.lastName ?: ""
            phoneNumber = value?.phoneNumber ?: ""
            mailId = value?.emailAddress ?: ""
        }

    //search contact
    private val _searchResult = MutableStateFlow<List<Contact>>(emptyList())
    val searchResult: StateFlow<List<Contact>> get() = _searchResult

    fun searchContact(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                _searchResult.value = listOf()
                return@launch
            }

            val searchResultFlow = contactDb.dao.search(query).map { data ->
                data.map { it.toContact() }
            }

            val localContactListFlow =
                flowOf(
                    localContactList.filter { it.name.contains(query, ignoreCase = true) }
                )

            searchResultFlow.combine(localContactListFlow) { searchResult, otherList ->
                (searchResult + otherList).sortedBy { it.firstName }
            }.collectLatest {
                _searchResult.value = it
            }
        }
    }

    fun clearSearchResultContacts() {
        viewModelScope.launch {
            _searchResult.value = listOf()
        }
    }

    //create edit contact.
    fun saveContact(): Contact? {
        val contact = Contact(
            id = (selectedContact?.id) ?: -1,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            emailAddress = mailId,
            profilePictureUrl = selectedContact?.profilePictureUrl,
            isRemote = selectedContact?.isRemote ?: false
        )
        Log.d(TAG, "ContactsViewmodel :: Contact need to save $contact")

        if (contact.isRemote) {
            viewModelScope.launch(Dispatchers.IO) {
                contactDb.dao.update(contactEntity = contact.toContactEntity())
            }
            selectedContact = contact
            return contact
        }

        return if (selectedContact?.id == null) {
            localContactsRepo.insertContact(contact = contact)
        } else {
            localContactsRepo.updateContact(contact = contact).also {
                selectedContact = it
            }
        }

    }

    var firstName = ""
    var lastName = ""
    var mailId = ""
    var phoneNumber = ""

    fun doSaveContactValidation(context: Context): String {
        return when {
            firstName.isEmpty() -> {
                context.getString(R.string.err_first_name_required)
            }

            lastName.isEmpty() -> {
                context.getString(R.string.err_last_name_required)
            }

            phoneNumber.isEmpty() -> {
                context.getString(R.string.err_phone_required)
            }

            !(Patterns.PHONE.matcher(phoneNumber).matches()) -> {
                context.getString(R.string.err_enter_valid_mobile)
            }

            mailId.isNotEmpty() && !(Patterns.EMAIL_ADDRESS.matcher(mailId).matches()) -> {
                context.getString(R.string.err_enter_valid_email)
            }

            else -> ""
        }
    }
}