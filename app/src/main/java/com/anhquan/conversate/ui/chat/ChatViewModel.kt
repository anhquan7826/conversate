package com.anhquan.conversate.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.data.AppContainer
import com.anhquan.conversate.model.Conversation
import com.anhquan.conversate.model.Message
import com.anhquan.conversate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel(private val container: AppContainer) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    var user by mutableStateOf<User?>(null)
    var conversation by mutableStateOf<Conversation?>(null)
    val messages = mutableStateListOf<Message>()

    var messageContent by mutableStateOf("")

    suspend fun loadConversation(conversationId: String?, email: String?) {
        user = null
        conversation = null
        messages.clear()
        messageContent = ""

        _uiState.update {
            it.copy(status = LoadingStatus.Loading)
        }
        conversation = if (conversationId != null) {
            container.conversations.getConversationByID(conversationId)
        } else if (email != null) {
            container.conversations.getConversationByContact(email)
        } else {
            null
        }
        user = container.users.getUserFromEmail(conversation?.getOthersEmail() ?: "")
        if (conversation == null) {
            _uiState.update {
                it.copy(status = LoadingStatus.Error)
            }
        } else {
            addListener(conversation?.id ?: "")
            _uiState.update {
                it.copy(
                    status = LoadingStatus.Loaded,
                    messages = messages,
                    conversation = conversation
                )
            }
        }
    }

    private suspend fun addListener(conversationId: String) {
        container.messages.messageListener(conversationId) { message ->
            messages.add(0, message)
            _uiState.update {
                it.copy(newMessage = message)
            }
        }
    }

    fun onContentChange(value: String) {
        messageContent = value
    }

    suspend fun onSend() {
        if (messageContent.trim().isNotEmpty()) {
            container.messages.sendMessage(
                conversationID = conversation!!.id, message = messageContent.trim()
            )
            messageContent = ""
        }
    }

    suspend fun onBack() {
        container.messages.cancelListener()
    }
}