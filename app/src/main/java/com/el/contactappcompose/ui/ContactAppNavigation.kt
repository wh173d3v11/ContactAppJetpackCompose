package com.el.contactappcompose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.el.contactappcompose.ui.contactcreateedit.CreateOrEditContactScreen
import com.el.contactappcompose.ui.contactdetails.DetailsScreen
import com.el.contactappcompose.ui.contactscreen.HomeScreen
import com.el.contactappcompose.vm.ContactsViewModel

@Composable
fun ContactAppNavigation() {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController(),
        LocalContactsViewModel provides hiltViewModel()
    ) {
        SetupNavGraph()
    }
}

@Composable
fun SetupNavGraph() {
    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = Routes.HOME.routeName,
    ) {

        composable(Routes.HOME.routeName) {
            HomeScreen()
        }

        composable(Routes.DETAIL_SCREEN.routeName) {
            DetailsScreen(
                onBackClicked = { navController.popBackStack() },
                onEditClicked = {

                    navController.navigate(Routes.CREATE_OR_EDIT_SCREEN.routeName)
                })
        }

        composable(Routes.CREATE_OR_EDIT_SCREEN.routeName) {
            CreateOrEditContactScreen(
                onBackClicked = { navController.popBackStack() })
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

val LocalContactsViewModel = compositionLocalOf<ContactsViewModel> {
    error("No LocalContactsViewModel provided")
}

enum class Routes(val routeName: String) {
    HOME("home"),
    DETAIL_SCREEN("detailScreen"),
    CREATE_OR_EDIT_SCREEN("createEditScreen")
}