package com.el.contactappcompose.ui.contactscreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.el.contactappcompose.domain.Contact

@Composable
fun ContactScreen(
    contacts: LazyPagingItems<Contact>
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = contacts.loadState) {
        val refresh = contacts.loadState.refresh
        if (refresh is LoadState.Error) { // checking for initial load and manual refresh error only.
            Toast.makeText(context, "Error " + refresh.error.message, Toast.LENGTH_SHORT).show()
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (contacts.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(
                    count = contacts.itemCount,
                    key = contacts.itemKey { it.id }
                ) { index ->
                    contacts[index]?.let { ContactItem(contact = it) }
                }

                item {
                    if (contacts.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

}