package com.el.contactappcompose.ui.components

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.el.contactappcompose.R
import com.el.contactappcompose.TAG
import com.el.contactappcompose.ui.LocalContactsViewModel
import com.el.contactappcompose.ui.LocalNavController
import com.el.contactappcompose.ui.Routes
import com.el.contactappcompose.ui.contactscreen.ContactItem
import com.el.contactappcompose.ui.theme.ContactAppComposeTheme

@Composable
fun ExpandableSearchView(
    modifier: Modifier = Modifier,
    expandedInitially: Boolean = false,
    tint: Color = MaterialTheme.colorScheme.onPrimary
) {
    val (expanded, onExpandedChanged) = rememberSaveable {
        mutableStateOf(expandedInitially)
    }

    Crossfade(targetState = expanded, label = "") { isSearchFieldVisible ->
        when (isSearchFieldVisible) {
            true -> ExpandedSearchView(
                onExpandedChanged = onExpandedChanged,
                modifier = modifier,
                tint = tint
            )

            false -> CollapsedSearchView(
                onExpandedChanged = onExpandedChanged,
                modifier = modifier,
                tint = tint
            )
        }
    }
}

@Composable
fun CollapsedSearchView(
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp)
        )
        IconButton(onClick = { onExpandedChanged(true) }) {
            Icon(
                imageVector = Icons.TwoTone.Search,
                contentDescription = "Search Icon",
                tint = tint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSearchView(
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var isSearchClicked by rememberSaveable { mutableStateOf(false) }

    val nc = LocalNavController.current
    val vm = LocalContactsViewModel.current
    val fm = LocalFocusManager.current

    val searchResult by vm.searchResult.collectAsState(initial = listOf())
    val searchFocusRequester = remember { FocusRequester() }

    val closeExpandedSearch = {
        vm.clearSearchResultContacts()
        onExpandedChanged(false)
    }

    SideEffect {
        searchFocusRequester.requestFocus()
    }

    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(searchFocusRequester),
        query = text,
        onQueryChange = {
            isSearchClicked = false
            text = it
        },
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        closeExpandedSearch.invoke()
                    },
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "back icon",
                tint = tint
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        isSearchClicked = false
                        text = ""
                    },
                imageVector = Icons.TwoTone.Clear,
                contentDescription = "clear icon",
                tint = tint
            )
        },
        onSearch = {
            isSearchClicked = true
            vm.searchContact(it)
            fm.clearFocus()
        },
        active = true,
        onActiveChange = {
            if (!it) {
                closeExpandedSearch.invoke()
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search_placeholder))
        }) {
        Log.i(TAG, "Searchbar :: searchList size = ${searchResult.size}")
        Text(
            modifier = Modifier.padding(8.dp),
            text = when {
                searchResult.isEmpty() and !isSearchClicked -> {
                    stringResource(id = R.string.search_result_hint)
                }

                searchResult.isEmpty() -> {
                    stringResource(id = R.string.search_result_not_found)
                }

                else -> {
                    stringResource(id = R.string.search_result_found)
                }
            }
        )

        if (searchResult.isNotEmpty()) {
            LazyColumn {
                items(searchResult) {
                    ContactItem(
                        modifier = Modifier.clickable {
                            vm.selectedContact = it
                            nc.navigate(Routes.DETAIL_SCREEN.routeName)
                        },
                        contact = it,
                        showLabel = true
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun CollapsedSearchViewPreview() {
    ContactAppComposeTheme {
//        ExpandedSearchView({}) //due to vm, preview not show.
    }
}

@Preview
@Composable
fun ExpandedSearchViewPreview() {
    ContactAppComposeTheme {
        CollapsedSearchView({})
    }
}