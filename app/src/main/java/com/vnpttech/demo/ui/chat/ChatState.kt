package com.vnpttech.demo.ui.chat

import com.vnpttech.demo.constants.LoadingStatus
import com.vnpttech.demo.model.Conversation
import com.vnpttech.demo.model.Message

data class ChatState(
    val status: LoadingStatus = LoadingStatus.Loading,
    val conversation: Conversation? = null,
    val messages: List<Message>? = null,
    val newMessage: Message? = null,
)