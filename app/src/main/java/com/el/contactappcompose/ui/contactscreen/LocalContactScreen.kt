package com.el.contactappcompose.ui.contactscreen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.el.contactappcompose.R
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.ui.LocalContactsViewModel


@Composable
fun LocalContactScreen(onContactClick: ((Contact) -> Unit)) {
    var result by remember { mutableStateOf<List<Contact>?>(null) }
    val context = LocalContext.current
    val vm = LocalContactsViewModel.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                result = vm.localContactList
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.err_contact_read_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    //checking and request permission
    val permission = Manifest.permission.READ_CONTACTS
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        result = vm.localContactList
    } else {
        SideEffect {
            requestPermissionLauncher.launch(permission)
        }
    }

    if (result.isNullOrEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            text = stringResource(id = R.string.text_no_contacts_found_create_new_one)
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        result?.let { contactlist ->
            items(
                count = contactlist.size,
            ) { contactIndex ->
                contactlist[contactIndex].let {
                    ContactItem(
                        modifier = Modifier.clickable { onContactClick.invoke(it) },
                        contact = it
                    )
                }
            }
        }
    }
}