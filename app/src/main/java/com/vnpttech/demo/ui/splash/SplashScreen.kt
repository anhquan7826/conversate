package com.vnpttech.demo.ui.splash

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.vnpttech.demo.data.AppContainer

@Composable
fun SplashScreen(navController: NavController, container: AppContainer) {
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