package com.vnpttech.demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vnpttech.demo.R
import com.vnpttech.demo.data.AppContainer
import com.vnpttech.demo.ui.AuthViewModel
import com.vnpttech.demo.ui.chat.ChatScreen
import com.vnpttech.demo.ui.chat.ChatViewModel
import com.vnpttech.demo.ui.home.HomeScreen
import com.vnpttech.demo.ui.home.contacts.ContactViewModel
import com.vnpttech.demo.ui.home.contacts.requests.AddContactScreen
import com.vnpttech.demo.ui.home.contacts.requests.ContactRequestScreen
import com.vnpttech.demo.ui.home.conversations.ConversationViewModel
import com.vnpttech.demo.ui.login.LoginScreen
import com.vnpttech.demo.ui.profile.ProfileScreen
import com.vnpttech.demo.ui.splash.SplashScreen

@Composable
fun NavigationView(
    container: AppContainer
) {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel(
        container = container,
        token = stringResource(R.string.default_web_client_id)
    )
    val contactViewModel = ContactViewModel(container)
    val conversationViewModel = ConversationViewModel(container)
    val chatViewModel = ChatViewModel(container)
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                container = container, navController = navController
            )
        }
        composable("home") {
            HomeScreen(
                container = container,
                navController = navController,
                contactViewModel = contactViewModel,
                conversationViewModel = conversationViewModel,
                authViewModel = authViewModel
            )
        }
        composable("contact_requests") {
            ContactRequestScreen(viewModel = contactViewModel, navController = navController)
        }
        composable("contact_add") {
            AddContactScreen(viewModel = contactViewModel, navController = navController)
        }
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel, navController = navController
            )
        }
        composable(
            "chat?conversationId={conversationId}&email={email}",
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("email") {
                    type = NavType.StringType
                    nullable = true
                },
            )
        ) {
            val conversationId = it.arguments?.getString("conversationId")
            val email = it.arguments?.getString("email")
            ChatScreen(
                conversationId = conversationId,
                email = email,
                navController = navController,
                viewModel = chatViewModel
            )
        }
        composable(
            "profile/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = it.arguments?.getString("email") ?: ""
            ProfileScreen(
                email = email, navController = navController, container = container
            )
        }
    }
}

