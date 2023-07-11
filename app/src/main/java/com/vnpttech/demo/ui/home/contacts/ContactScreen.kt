package com.vnpttech.demo.ui.home.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vnpttech.demo.R
import com.vnpttech.demo.constants.LoadingStatus
import com.vnpttech.demo.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel, navController: NavController,
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(true) {
        viewModel.loadContacts()
    }
    if (showDialog) {
        ContactDeleteDialog(
            onAccept = {
                scope.launch {
                    viewModel.onContactDeleteAccept()
                    showDialog = false
                }
            },
            onDismiss = {
                viewModel.onContactDeleteCancel()
                showDialog = false
            },
        )
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(id = R.string.contact_title))
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate("contact_requests") {
                    restoreState = true
                }
            }) {
                Box {
                    Icon(Icons.Rounded.Person, contentDescription = null)
                    if (state.requestAvailable) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, shape = CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }, actions = {
            IconButton(onClick = {
                viewModel.onFilterToggle()
            }) {
                Icon(Icons.Rounded.Search, contentDescription = null)
            }
            IconButton(onClick = {
                navController.navigate("contact_add") {
                    restoreState = true
                }
            }) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        })
    }) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            Column {
                if (viewModel.toggleFilter) {
                    OutlinedTextField(
                        value = viewModel.filter,
                        onValueChange = { value ->
                            viewModel.onFilterChange(value)
                        },
                        placeholder = {
                            Text(stringResource(id = R.string.search_conversation))
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.onFilterToggle()
                            }) {
                                Icon(Icons.Rounded.Close, contentDescription = null)
                            }
                        },
                        shape = CircleShape,
                        maxLines = 1,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                }
                when (state.status) {
                    LoadingStatus.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    LoadingStatus.Loaded -> {
                        if (viewModel.contacts.isEmpty()) {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    stringResource(id = R.string.contact_empty),
                                    color = Color.Gray,
                                    modifier = Modifier.align(
                                        Alignment.Center
                                    )
                                )

                            }
                        } else {
                            LazyColumn {
                                items(viewModel.contacts.size) { index ->
                                    val contact = viewModel.contacts[index]
                                    ContactTile(
                                        user = contact,
                                        onTap = {
                                            navController.navigate("chat?email=${contact.email}")
                                        },
                                        onDeleteTap = {
                                            showDialog = true
                                            viewModel.onContactDeletePending(contact)
                                        },
                                    )
                                }
                            }
                        }
                    }

                    LoadingStatus.Error -> {}
                }
            }
        }
    }
}

@Composable
fun ContactDeleteDialog(
    onAccept: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.contact_delete_alert_title)) },
        text = { Text(stringResource(id = R.string.contact_delete_alert_text)) },
        confirmButton = {
            TextButton(
                onClick = onAccept
            ) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun ContactTile(user: User, onTap: () -> Unit, onDeleteTap: () -> Unit) {
    Box(
        Modifier
            .clickable {
                onTap()
            }
            .padding(
                horizontal = 4.dp, vertical = 6.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                user.avatar,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
            )
            Text(
                text = user.displayName,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = onDeleteTap) {
                Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}