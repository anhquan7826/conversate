package com.vnpttech.demo.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.vnpttech.demo.R
import com.vnpttech.demo.firebase.rememberFirebaseAuthLauncher
import com.vnpttech.demo.ui.AuthState
import com.vnpttech.demo.ui.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel, navController: NavHostController
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
            authViewModel.onAuthResult(it)
        },
        onAuthError = {
            authViewModel.onAuthError(it)
        },
    )
    if (state == AuthState.SUCCEEDED) {
        navController.navigate("home") {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
        authViewModel.onRoutePopped()
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(id = R.string.app_name), fontSize = 32.sp, lineHeight = 34.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(64.dp))
            Text(stringResource(id = R.string.welcome_text))
            Spacer(modifier = Modifier.height(28.dp))
            Button(onClick = {
                launcher.launch(GoogleSignIn.getClient(context, authViewModel.gso).signInIntent)
            }) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = null,
                        modifier = Modifier.width(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.signin_google))
                }
            }
        }
    }
}