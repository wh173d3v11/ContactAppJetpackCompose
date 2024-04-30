package com.el.contactappcompose.ui.contactcreateedit

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.el.contactappcompose.R
import com.el.contactappcompose.TAG
import com.el.contactappcompose.ui.LocalContactsViewModel


@Composable
fun CreateOrEditContactScreen(onBackClicked: () -> Unit) {
    val vm = LocalContactsViewModel.current
    val context = LocalContext.current
    val fm = LocalFocusManager.current

    val contact = vm.selectedContact

    val titleHeader = if (contact == null) {
        stringResource(id = R.string.txt_create)
    } else {
        stringResource(
            id = R.string.txt_edit
        )
    }

    val doValidation = {
        val err = vm.doSaveContactValidation(context)
        if (err.isNotEmpty()) Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
        else {
            fm.clearFocus()
            val c = vm.saveContact()
            if (c == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.err_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.txt_contact_saved),
                    Toast.LENGTH_SHORT
                ).show()
                onBackClicked.invoke()
            }
        }
    }

    RequestWritePermissionIfNeeded()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            Modifier
                .padding(bottom = 40.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClicked() },
                imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .offset((-24).dp), // above image size to make it center
                text = titleHeader,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                textAlign = TextAlign.Center
            )
        }

        ContactTextField(
            modifier = Modifier,
            value = contact?.firstName ?: "",
            label = stringResource(id = R.string.label_firstName),
            placeHolder = stringResource(id = R.string.placeholder_firstName),
            leadingIcon = Icons.Filled.AccountCircle,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = {
                vm.firstName = it
            }
        )

        ContactTextField(
            modifier = Modifier,
            value = contact?.lastName ?: "",
            label = stringResource(id = R.string.label_lastName),
            placeHolder = stringResource(id = R.string.placeholder_lastName),
            leadingIcon = Icons.Filled.AccountCircle,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = {
                vm.lastName = it
            }
        )

        ContactTextField(
            modifier = Modifier,
            value = contact?.phoneNumber ?: "",
            label = stringResource(id = R.string.label_phone),
            placeHolder = stringResource(id = R.string.placeholder_phone),
            leadingIcon = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            maxLength = 20,
            onValueChange = {
                vm.phoneNumber = it
            }
        )
        ContactTextField(
            modifier = Modifier,
            value = contact?.emailAddress ?: "",
            label = stringResource(id = R.string.label_email),
            placeHolder = stringResource(id = R.string.placeholder_email),
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            maxLength = 40,
            requiredField = false,
            onValueChange = {
                vm.mailId = it
            }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                doValidation()
            }) {
            Text(text = stringResource(id = R.string.txt_save))
        }

    }
}

@Composable
fun RequestWritePermissionIfNeeded() {
    val context = LocalContext.current
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "CreateContact :: Write permission granted.")
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.err_contact_write_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    //checking and request permission
    val permission = Manifest.permission.WRITE_CONTACTS
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Log.d(TAG, "CreateContact :: Write permission granted.")
    } else {
        SideEffect {
            requestPermissionLauncher.launch(permission)
        }
    }
}

@Composable
fun ContactTextField(
    modifier: Modifier,
    value: String,
    label: String,
    placeHolder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    maxLength: Int = 20,
    requiredField: Boolean = true,
    onValueChange: (String) -> Unit
) {

    var filledText by rememberSaveable { mutableStateOf(value) }

    val errMaxCharReached = stringResource(id = R.string.max_length_reached)
    val errEmptyFieldReached = stringResource(id = R.string.field_should_not_be_empty)

    var errorText by rememberSaveable { mutableStateOf("") }

    val setFilledValue: (String) -> Unit = {
        filledText = it

        if (errorText.isEmpty() and filledText.isNotEmpty())
            onValueChange.invoke(filledText)
        else onValueChange.invoke("")
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (!it.isFocused && errorText == errMaxCharReached) errorText = ""
            },
        value = filledText,
        onValueChange = {
            when {
                requiredField && it.trim().isEmpty() -> {
                    errorText = errEmptyFieldReached
                    setFilledValue.invoke(it)
                }

                it.length > maxLength -> {
                    errorText = errMaxCharReached
                }

                else -> {
                    errorText = ""
                    setFilledValue.invoke(it)
                }
            }
        },
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeHolder, color = MaterialTheme.colorScheme.surfaceTint)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label
            )
        },
        supportingText = {
            if (errorText.isNotEmpty())
                Text(text = errorText)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        isError = requiredField && errorText.isNotEmpty(),
        singleLine = true
    )
}