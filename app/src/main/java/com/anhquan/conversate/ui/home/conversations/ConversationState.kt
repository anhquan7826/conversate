package com.anhquan.conversate.ui.home.conversations

import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.constants.UpdateType

data class ConversationState(
    val status: LoadingStatus,
    val update: UpdateType? = null,
    val updatedValue: String? = null,
)