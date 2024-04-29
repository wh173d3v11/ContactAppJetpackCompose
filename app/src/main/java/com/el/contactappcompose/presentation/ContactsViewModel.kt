package com.el.contactappcompose.presentation

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.el.contactappcompose.R
import com.el.contactappcompose.data.local.ContactDatabase
import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.toContact
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.utils.LocalContactUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ContactsViewModel @Inject constructor(
    pager: Pager<Int, ContactEntity>,
    val contactDb: ContactDatabase,
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
        get() = LocalContactUtils.savedLocalContacts

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
        viewModelScope.launch {
            if (query.isEmpty()) {
                _searchResult.value = listOf()
                return@launch
            }
            val searchResultFlow = contactDb.dao.search(query).map { data ->
                data.map { it.toContact() }
            }

            val localContactListFlow =
                flowOf(localContactList.filter { it.name.contains(query, ignoreCase = true) })

            searchResultFlow.combine(localContactListFlow) { searchResult, otherList ->
                (searchResult + otherList).sortedBy { it.firstName }
            }.onEach {
                _searchResult.value = it // Update the MutableStateFlow with the sorted list
            }
        }
    }

    fun clearSearchResultContacts() {
        viewModelScope.launch {
            _searchResult.value = listOf()
        }
    }

    //create edit contact.
    fun saveContact(context: Context) {
        val contact = Contact(
            id = (selectedContact?.id) ?: (phoneNumber.hashCode() + Random.nextInt()),
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            emailAddress = mailId,
            profilePictureUrl = null,
            isRemote = false
        )
        Log.d("contact", "Contact need to save $contact")
//        return LocalContactUtils.getNewContactIntent(contact = contact)

        LocalContactUtils.updateContact(context = context, contact = contact)

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