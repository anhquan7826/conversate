package com.vnpttech.demo.ui.home.contacts.requests

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vnpttech.demo.R
import com.vnpttech.demo.constants.AddContactStatus
import com.vnpttech.demo.ui.home.contacts.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(viewModel: ContactViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val onBack = {
        navController.popBackStack()
    }
    BackHandler(enabled = true) {
        onBack()
    }
    when (state.addContactStatus) {
        AddContactStatus.Invalid -> {
            LaunchedEffect(true) {
                snackBarHostState.showSnackbar(
                    message = "User with email '${viewModel.emailField}' is not exist!",
                    duration = SnackbarDuration.Short,
                )
            }
        }
        AddContactStatus.Done -> {
            LaunchedEffect(true) {
                snackBarHostState.showSnackbar(
                    message = "Sent contact request to '${viewModel.emailField}'!",
                    duration = SnackbarDuration.Short,
                )
            }
        }
        AddContactStatus.None -> {}
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(id = R.string.contact_add_title))
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) {
        Box(Modifier.padding(it)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = viewModel.emailField,
                    onValueChange = { value ->
                        viewModel.onEmailChange(value)
                    },
                    label = {
                        Text(stringResource(id = R.string.contact_add_email_label))
                    },
                    placeholder = {
                        Text(stringResource(id = R.string.contact_add_email_hint))
                    },
                    singleLine = true,
                    isError = state.addContactStatus == AddContactStatus.Invalid,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp,
                        ),
                )
                TextField(
                    value = viewModel.messageField,
                    onValueChange = { value ->
                        viewModel.onMessageChange(value)
                    },
                    label = {
                        Text(stringResource(id = R.string.contact_add_message_label))
                    },
                    placeholder = {
                        Text(stringResource(id = R.string.contact_add_message_hint))
                    },
                    singleLine = false,
                    minLines = 10,
                    maxLines = 10,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
                        ),
                )
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.sendContactRequest()
                        }
                    },
                    enabled = viewModel.emailField.isNotEmpty(),
                ) {
                    Text(stringResource(id = R.string.contact_add_send_request))
                }
            }
        }
    }
}