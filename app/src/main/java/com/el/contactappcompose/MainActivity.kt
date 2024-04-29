package com.el.contactappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.el.contactappcompose.ui.ContactAppNavigation
import com.el.contactappcompose.ui.theme.ContactAppComposeTheme
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "ContactAppCompose"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactAppComposeTheme {
                ContactAppNavigation()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContactAppComposeTheme {
        Greeting("Android")
    }
}