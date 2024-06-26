package com.el.contactappcompose.ui.contactscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.el.contactappcompose.R
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.ui.theme.ContactAppComposeTheme

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    contact: Contact, showLabel: Boolean = false//this will show form remote or local.
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .clip(CircleShape),
            error = painterResource(R.drawable.placeholder),
            model = contact.profilePictureUrl ?: "",
            contentDescription = "User profile Picture"
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
            if (showLabel) Text(
                text = contact.labelName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
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
            ), showLabel = true
        )
    }
}