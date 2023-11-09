package com.anhquan.conversate.ui.home.contacts

import com.anhquan.conversate.constants.AddContactStatus
import com.anhquan.conversate.constants.LoadingStatus

data class ContactState(
    val status: LoadingStatus,
    val requestAvailable: Boolean = false,
    val requestSize: Int = -1,
    val addContactStatus: AddContactStatus = AddContactStatus.None,
    val deletedContactEmail: String? = null
)