package com.el.contactappcompose.ui.contactscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.el.contactappcompose.presentation.ContactsViewModel


@Composable
fun ContactScreen(modifier: Modifier) {
    val viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>()
    val contacts = viewModel.contactPagingFlow.collectAsLazyPagingItems()

    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Local", "Remote")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(
            selectedTabIndex = tabIndex,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(text = {
                    Text(title)
                }, selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> LocalContactScreen()
            1 -> RemoteContactScreen(contacts = contacts)
        }
    }
}