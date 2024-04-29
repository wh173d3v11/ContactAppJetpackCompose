package com.el.contactappcompose.ui.contactdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.el.contactappcompose.R
import com.el.contactappcompose.domain.Contact
import com.el.contactappcompose.ui.LocalContactsViewModel
import com.el.contactappcompose.ui.theme.ContactAppComposeTheme

@Composable
fun DetailsScreen(
    onBackClicked: () -> Unit, onEditClicked: (Contact) -> Unit
) {
    val vm = LocalContactsViewModel.current
    Box(modifier = Modifier.fillMaxSize()) {
        vm.selectedContact?.let {
            ContactDetailsScreen(it,
                onBackClicked = onBackClicked,
                onEditClicked = { onEditClicked.invoke(it) })
        } ?: Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.text_contact_not_found)
        )
    }
}

@Composable
fun ContactDetailsScreen(
    contact: Contact, onBackClicked: () -> Unit = {}, onEditClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val profileOffset = 24.dp
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            val (profBg, profImage, editIcon, ivBack) = createRefs()

            AsyncImage(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(10.dp)
                    .alpha(0.6f)
                    .constrainAs(profBg) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },
                error = painterResource(R.drawable.placeholder),
                placeholder = painterResource(R.drawable.placeholder),
                model = contact.profilePictureUrl ?: "",
                contentDescription = "User profile Picture"
            )
            AsyncImage(
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .offset(0.dp, profileOffset)
                    .clip(CircleShape)
                    .shadow(5.dp)
                    .constrainAs(profImage) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                error = painterResource(R.drawable.placeholder),
                placeholder = painterResource(R.drawable.placeholder),
                model = contact.profilePictureUrl ?: "",
                contentDescription = "User profile Picture"
            )

            Icon(modifier = Modifier
                .size(28.dp)
                .constrainAs(ivBack) {
                    top.linkTo(parent.top, margin = 24.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                }
                .clickable { onBackClicked() },
                imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint
            )

            Icon(modifier = Modifier
                .size(28.dp)
                .constrainAs(editIcon) {
                    top.linkTo(parent.top, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                }
                .clickable { onEditClicked() },
                imageVector = Icons.TwoTone.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }

        Text(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            text = contact.name,
            modifier = Modifier.padding(top = profileOffset)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 10.dp)
        ) {
            ShowIconCard(
                modifier = Modifier.weight(1f), title = "Call", icon = Icons.TwoTone.Call
            )
            ShowIconCard(
                modifier = Modifier.weight(1f), title = "Message", icon = Icons.TwoTone.Send
            )
            ShowIconCard(
                modifier = Modifier.weight(1f), title = "Mail", icon = Icons.TwoTone.Email
            )
            ShowIconCard(
                modifier = Modifier.weight(1f), title = "Favourite", icon = Icons.TwoTone.Favorite
            )
        }

        ShowDetailCard(
            title = "Mobile", value = contact.phoneNumber
        )

        if (contact.emailAddress.isNotEmpty()) {
            ShowDetailCard(
                title = "Email Address", value = contact.emailAddress
            )
        }
//        Text(text = "ss")

    }
}

@Composable
fun ShowDetailCard(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
        ) {
            Text(text = title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            Text(text = value)
        }
    }
}

@Composable
fun ShowIconCard(modifier: Modifier = Modifier, title: String, icon: ImageVector) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = title,
                style = TextStyle(fontWeight = FontWeight.Light)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactDetailsScreenPreview() {
    ContactAppComposeTheme {
        ContactDetailsScreen(
            contact = Contact(
                id = 1,
                firstName = "Five",
                lastName = "Hargreeves️",
                phoneNumber = "9876543210",
                emailAddress = "five@umbrellaacademy.com",
                profilePictureUrl = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowDetailCardPreview() {
    ContactAppComposeTheme {
        ShowDetailCard(modifier = Modifier, "Mobile", "12345")
    }
}
