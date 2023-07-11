package com.vnpttech.demo.model

data class User(
    val avatar: String?,
    val displayName: String,
    val email: String,

    val contacts: MutableList<User> = mutableListOf(),
    val archivedContacts: MutableList<User> = mutableListOf(),
)