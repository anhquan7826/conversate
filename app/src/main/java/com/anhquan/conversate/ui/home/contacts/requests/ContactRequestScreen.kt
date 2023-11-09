package com.anhquan.conversate.ui.home.contacts.requests

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.anhquan.conversate.R
import com.anhquan.conversate.model.User
import com.anhquan.conversate.ui.home.contacts.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactRequestScreen(viewModel: ContactViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val onBack = {
        navController.popBackStack()
    }
    BackHandler(enabled = true) {
        onBack()
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(id = R.string.contact_request_title))
        }, navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            if (viewModel.contactRequests.size <= 0) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        stringResource(id = R.string.contact_request_empty),
                        color = Color.Gray,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }
            } else {
                LazyColumn {
                    items(viewModel.contactRequests.size) { index ->
                        ContactRequestTile(
                            user = viewModel.contactRequests[index].user,
                            message = viewModel.contactRequests[index].requestMessage,
                            onAccept = {
                                scope.launch {
                                    viewModel.requestOperation(
                                        viewModel.contactRequests[index], true
                                    )
                                }
                            },
                            onReject = {
                                scope.launch {
                                    viewModel.requestOperation(
                                        viewModel.contactRequests[index], false
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactRequestTile(user: User, message: String, onAccept: () -> Unit, onReject: () -> Unit) {
    var toggleMessageDialog by remember { mutableStateOf(false) }
    if (toggleMessageDialog) {
        AlertDialog(
            onDismissRequest = {
                toggleMessageDialog = false
            },
            title = {
                Text(
                    stringResource(
                        id = R.string.contact_request_message_header, user.displayName
                    ),
                )
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(onClick = { toggleMessageDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {},
        )
    }
    Box(
        Modifier
            .padding(
                horizontal = 4.dp, vertical = 6.dp
            )
            .clickable {
                if (message.isNotEmpty()) {
                    toggleMessageDialog = true
                }
            }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
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
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = user.displayName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (message.isNotEmpty()) {
                    Text(
                        message, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onAccept) {
                Icon(Icons.Rounded.Done, contentDescription = null)
            }
            IconButton(onClick = onReject) {
                Icon(Icons.Rounded.Close, contentDescription = null)
            }
        }
    }
}