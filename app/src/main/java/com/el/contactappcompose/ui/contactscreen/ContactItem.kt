package com.el.contactappcompose.ui.contactscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.ui.theme.ContactAppComposeTheme

@Composable
fun ContactItem(
    contact: Contact,
    modifier: Modifier = Modifier,
    onContactClick: (Contact) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onContactClick(contact) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = contact.profilePictureUrl ?: "",
            contentDescription = "User profile Picture"
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactItemPreview() {
    ContactAppComposeTheme {
        ContactItem(
            contact = Contact(
                id = 1,
                firstName = "Vanya",
                lastName = "Hargreeves ❤️",
                phoneNumber = "9876543210",
                emailAddress = "wow@umbrellaacademy.com",
                profilePictureUrl = null
            )
        )
    }
}