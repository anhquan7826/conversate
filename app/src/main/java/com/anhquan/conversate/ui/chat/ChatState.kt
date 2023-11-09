package com.anhquan.conversate.ui.chat

import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.model.Conversation
import com.anhquan.conversate.model.Message

data class ChatState(
    val status: LoadingStatus = LoadingStatus.Loading,
    val conversation: Conversation? = null,
    val messages: List<Message>? = null,
    val newMessage: Message? = null,
)