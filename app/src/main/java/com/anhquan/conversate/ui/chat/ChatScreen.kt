package com.anhquan.conversate.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.anhquan.conversate.R
import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.data.CacheData
import com.anhquan.conversate.model.Message
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    email: String?,
    navController: NavController,
    viewModel: ChatViewModel,
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var s = remember { mutableStateOf("") }
    LaunchedEffect(true) {
        viewModel.loadConversation(conversationId, email)
    }
    val onBack = {
        scope.launch {
            viewModel.onBack()
        }
        navController.popBackStack()
    }
    BackHandler(enabled = true) {
        onBack()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = viewModel.user?.displayName ?: "")
            }, navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
            }, actions = {
                AsyncImage(
                    model = viewModel.user?.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .clip(
                            CircleShape
                        )
                        .clickable {
                            navController.navigate("profile/${viewModel.user?.email}")
                        },
                )
            })
        },
        bottomBar = {
            val onSend: () -> Unit = {
                scope.launch {
                    viewModel.onSend()
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.messageContent,
                    minLines = 1, maxLines = 2,
                    shape = CircleShape,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(stringResource(id = R.string.message_field_hint))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        onSend()
                    }),
                    trailingIcon = {
                        IconButton(onClick = onSend, modifier = Modifier.padding(end = 8.dp)) {
                            Icon(Icons.Rounded.Send, contentDescription = null)
                        }
                    },
                    onValueChange = {
                        viewModel.onContentChange(it)
                    },
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (state.status) {
                LoadingStatus.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                LoadingStatus.Loaded -> {
                    if (viewModel.messages.isEmpty()) {
                        Text(
                            text = "Start conversation!", color = Color.Gray,
                            modifier = Modifier.align(
                                Alignment.Center
                            ),
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                            items(viewModel.messages.size) { index ->
                                val message = viewModel.messages[index]
                                MessageTile(message = message)
                            }
                        }
                    }
                }

                LoadingStatus.Error -> {

                }
            }
        }
    }
}

@Composable
fun MessageTile(message: Message) {
    val isMe = message.sender == CacheData.user!!.email
    val contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    Box(
        contentAlignment = contentAlignment, modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp,
            )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.7F),
            contentAlignment = contentAlignment,
        ) {
            Box(
                Modifier
                    .background(
                        if (isMe) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3F)
                        else MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = if (isMe) 10.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 10.dp,
                        )
                    )
                    .padding(
                        vertical = 8.dp, horizontal = 16.dp
                    )
            ) {
                Text(message.content)
            }
        }
    }
}