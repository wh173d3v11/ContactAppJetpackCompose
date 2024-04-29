package com.el.contactappcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

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

    //for detail screen
    var selectedContact: Contact? = null

    //search contact
    private val _searchResult = MutableStateFlow<List<Contact>>(emptyList())
    val searchResult: StateFlow<List<Contact>> get() = _searchResult

    fun searchContact(query: String = "") {
        if (query.isEmpty()) {
            _searchResult.value = listOf()
            return
        }
        val searchResultFlow = contactDb.dao.search(query).map { data ->
            data.map { it.toContact() }
        }
        val localContactListFlow = flowOf(localContactList.filter { it.name.contains(query, ignoreCase = true) })

        searchResultFlow.combine(localContactListFlow) { searchResult, otherList ->
            (searchResult + otherList).sortedBy { it.firstName }
        }.onEach {
            _searchResult.value = it // Update the MutableStateFlow with the sorted list
        }.launchIn(viewModelScope)
    }

}