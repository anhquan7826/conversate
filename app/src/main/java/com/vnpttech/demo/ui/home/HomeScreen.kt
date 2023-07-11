package com.vnpttech.demo.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vnpttech.demo.R
import com.vnpttech.demo.data.AppContainer
import com.vnpttech.demo.ui.AuthViewModel
import com.vnpttech.demo.ui.home.contacts.ContactScreen
import com.vnpttech.demo.ui.home.contacts.ContactViewModel
import com.vnpttech.demo.ui.home.conversations.ConversationScreen
import com.vnpttech.demo.ui.home.conversations.ConversationViewModel

@Composable
fun HomeScreen(
    container: AppContainer,
    navController: NavController,
    conversationViewModel: ConversationViewModel,
    contactViewModel: ContactViewModel,
    authViewModel: AuthViewModel
) {
    val childNavController = rememberNavController()
    var currentRoute by rememberSaveable { mutableStateOf("conversations") }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Rounded.Message, contentDescription = null)
                    },
                    label = {
                        Text(stringResource(id = R.string.navbar_conversation))
                    },
                    selected = currentRoute == "conversations",
                    onClick = {
                        currentRoute = "conversations"
                        childNavController.navigate("conversation") {
                            popUpTo(childNavController.graph.findStartDestination().id)
                        }
                    },
                )
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Rounded.Contacts, contentDescription = null)
                    },
                    label = {
                        Text(stringResource(id = R.string.navbar_contact))
                    },
                    selected = currentRoute == "contacts",
                    onClick = {
                        currentRoute = "contacts"
                        childNavController.navigate("contacts") {
                            popUpTo(childNavController.graph.findStartDestination().id)
                        }
                    },
                )
            }
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                NavHost(navController = childNavController, startDestination = "conversation") {
                    composable("conversation") {
                        ConversationScreen(
                            navController = navController,
                            viewModel = conversationViewModel,
                            authViewModel = authViewModel
                        )
                    }
                    composable("contacts") {
                        ContactScreen(viewModel = contactViewModel, navController = navController)
                    }
                }
            }
        },
    )
}
