package com.el.contactappcompose.ui.contactscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.presentation.ContactsViewModel
import com.el.contactappcompose.ui.LocalNavController
import com.el.contactappcompose.ui.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: ContactsViewModel) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Contacts")
            })
        },
        modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        ContactScreen(Modifier.padding(top = contentPadding.calculateTopPadding()),
            onContactClick = {
                vm.selectedContact = it
                Log.d("contact", "Contact Saved vm.selectedContact ${vm.selectedContact}")
                navController.navigate(Routes.DETAIL_SCREEN.name)
            })
    }
}

@Composable
fun ContactScreen(modifier: Modifier, onContactClick: ((Contact) -> Unit)) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

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
            0 -> LocalContactScreen(onContactClick = onContactClick)
            1 -> RemoteContactScreen(onContactClick = onContactClick)
        }
    }
}