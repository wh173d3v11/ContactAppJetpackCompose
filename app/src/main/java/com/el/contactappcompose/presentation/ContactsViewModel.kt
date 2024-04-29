package com.el.contactappcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.toContact
import com.el.contactappcompose.domain.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    pager: Pager<Int, ContactEntity>
) : ViewModel() {

    val contactPagingFlow = pager
        .flow
        .map { data ->
            data.map { it.toContact() }
        }
        .cachedIn(viewModelScope)

    var selectedContact: Contact? = null

}