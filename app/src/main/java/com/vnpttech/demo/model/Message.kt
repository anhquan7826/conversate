package com.vnpttech.demo.model

import java.time.Instant
import java.util.Date

data class Message(
    val id: String,
    val sender: String,
    val content: String,
    val timeStamp: Date = Date.from(Instant.now()),
)