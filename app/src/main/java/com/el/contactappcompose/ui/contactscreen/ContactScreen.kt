package com.el.contactappcompose.ui.contactscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.ui.LocalContactsViewModel
import com.el.contactappcompose.ui.LocalNavController
import com.el.contactappcompose.ui.Routes
import com.el.contactappcompose.ui.components.ExpandableSearchView


@Composable
fun HomeScreen() {

    val navController = LocalNavController.current
    val vm = LocalContactsViewModel.current

    Scaffold(
        topBar = {
            ExpandableSearchView(tint = Color.Black)
        },
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        ContactScreen(
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
            onContactClick = {
                vm.selectedContact = it
                Log.d("contact", "Contact Saved vm.selectedContact ${vm.selectedContact}")
                navController.navigate(Routes.DETAIL_SCREEN.routeName)
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