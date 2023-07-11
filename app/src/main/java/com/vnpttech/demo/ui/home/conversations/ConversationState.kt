package com.vnpttech.demo.ui.home.conversations

import com.vnpttech.demo.constants.LoadingStatus
import com.vnpttech.demo.constants.UpdateType

data class ConversationState(
    val status: LoadingStatus,
    val update: UpdateType? = null,
    val updatedValue: String? = null,
)