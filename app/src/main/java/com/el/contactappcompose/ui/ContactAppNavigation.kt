package com.el.contactappcompose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.el.contactappcompose.presentation.ContactsViewModel
import com.el.contactappcompose.ui.contactdetails.DetailsScreen
import com.el.contactappcompose.ui.contactscreen.HomeScreen

@Composable
fun ContactAppNavigation() {
    CompositionLocalProvider(LocalNavController provides rememberNavController()) {
        SetupNavGraph()
    }
}

@Composable
fun SetupNavGraph() {
    val navController = LocalNavController.current

    val vm = hiltViewModel<ContactsViewModel>()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME.name,
    ) {

        composable(Routes.HOME.name) {
            HomeScreen(vm)
        }

        composable(Routes.DETAIL_SCREEN.name) {
            DetailsScreen(vm, onBackClicked = { navController.popBackStack() })
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

enum class Routes(routeName: String) {
    HOME("home"),
    DETAIL_SCREEN("detailScreen")
}