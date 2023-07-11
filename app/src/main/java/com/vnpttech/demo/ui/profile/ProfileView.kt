package com.vnpttech.demo.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vnpttech.demo.R
import com.vnpttech.demo.constants.LoadingStatus
import com.vnpttech.demo.data.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    email: String,
    navController: NavController,
    container: AppContainer,
    viewModel: ProfileViewModel = remember { ProfileViewModel(container) },
) {
    val state by viewModel.uiState.collectAsState()
    val onBack = {
        navController.popBackStack()
    }
    LaunchedEffect(true) {
        viewModel.loadUser(email)
    }
    BackHandler(enabled = true) {
        onBack()
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(id = R.string.profile_title))
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBack()
                }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
            },
        )
    }) {
        when (state) {
            LoadingStatus.Loading -> {
                Box(
                    Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            LoadingStatus.Loaded -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val user = viewModel.user!!
                    Spacer(modifier = Modifier.height(32.dp))
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        user.displayName,
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(user.email, fontSize = 18.sp, textAlign = TextAlign.Center)
                }
            }

            LoadingStatus.Error -> {
                Text("Cannot load '$email' profile", color = Color.Gray)
            }
        }
    }
}