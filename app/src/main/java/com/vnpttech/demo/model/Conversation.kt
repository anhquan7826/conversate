package com.vnpttech.demo.model

import com.google.firebase.firestore.DocumentId
import com.vnpttech.demo.data.CacheData
import java.time.Instant
import java.util.Date

data class Conversation(
    val id: String,
    val people: List<String>,
    val latestMessage: String? = null,
    val latestTimeStamp: Date = Date.from(Instant.now())
) {
    fun getOthersEmail(): String {
        return people.find { it != CacheData.user!!.email }!!
    }
}