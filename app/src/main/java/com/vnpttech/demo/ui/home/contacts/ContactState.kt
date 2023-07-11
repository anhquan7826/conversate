package com.vnpttech.demo.ui.home.contacts

import com.vnpttech.demo.constants.AddContactStatus
import com.vnpttech.demo.constants.LoadingStatus

data class ContactState(
    val status: LoadingStatus,
    val requestAvailable: Boolean = false,
    val requestSize: Int = -1,
    val addContactStatus: AddContactStatus = AddContactStatus.None,
    val deletedContactEmail: String? = null
)