package com.anhquan.conversate.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.anhquan.conversate.data.AppContainer

@Composable
fun SplashScreen(navController: NavController, container: AppContainer) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        container.initDatabase(context)
    }
    if (container.users.isLoggedIn()) {
        navController.navigate("home") {
            popUpTo("splash") {
                inclusive = true
            }
        }
    } else {
        navController.navigate("login") {
            popUpTo("splash") {
                inclusive = true
            }
        }
    }
}