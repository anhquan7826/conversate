package com.anhquan.conversate.ui.home.conversations

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.anhquan.conversate.R
import com.anhquan.conversate.components.ShimmerAnimation
import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.data.CacheData
import com.anhquan.conversate.model.User
import com.anhquan.conversate.ui.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController, viewModel: ConversationViewModel, authViewModel: AuthViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        viewModel.loadConversation()
    }
    BackHandler(enabled = true) {
        scope.launch {
            viewModel.onBack()
            navController.popBackStack()
        }
    }
    if (showLogoutDialog) {
        LogOutDialog(onAccept = {
            authViewModel.logout(context)
            navController.navigate("login") {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }, onDismiss = {
            showLogoutDialog = false
        })
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    AsyncImage(model = CacheData.user!!.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp)
                            .clip(
                                CircleShape
                            )
                            .clickable {
                                navController.navigate("profile/${CacheData.user?.email}")
                            })
                },
                title = {
                    Text(stringResource(id = R.string.conversation_title))
                },
                actions = {
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Icon(Icons.Rounded.Logout, contentDescription = null)
                    }
                },
            )
        },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (state.status) {
                LoadingStatus.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                LoadingStatus.Loaded -> {
                    if (viewModel.conversations.isEmpty()) {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                stringResource(id = R.string.conversation_empty),
                                color = Color.Gray,
                                modifier = Modifier.align(
                                    Alignment.Center
                                )
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.conversations.size) { index ->
                                val c = viewModel.conversations[index]
                                ConversationTile(
                                    userEmail = c.getOthersEmail(),
                                    content = c.latestMessage ?: "Start conversations!",
                                    viewModel = viewModel
                                ) {
                                    navController.navigate("chat?conversationId=${c.id}")
                                }
                            }
                        }
                    }
                }

                LoadingStatus.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "cannot load", modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogOutDialog(
    onAccept: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.conversation_logout_dialog_title)) },
        text = { Text(stringResource(id = R.string.conversation_logout_dialog_content)) },
        confirmButton = {
            TextButton(
                onClick = onAccept
            ) {
                Text("Log out", color = Color.Red)
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
fun ConversationTile(
    userEmail: String, content: String, viewModel: ConversationViewModel, onTap: () -> Unit
) {
    var user: User? by remember { mutableStateOf(null) }
    LaunchedEffect(user) {
        if (user == null) {
            user = viewModel.getUser(userEmail)
        }
    }
    if (user == null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.dp, vertical = 6.dp
                )
        ) {
            ShimmerAnimation(
                Modifier
                    .size(64.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
            )
            Column {
                ShimmerAnimation(
                    Modifier
                        .width(128.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerAnimation(
                    Modifier
                        .width(230.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    } else {
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
                    user!!.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = user!!.displayName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(text = content, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}