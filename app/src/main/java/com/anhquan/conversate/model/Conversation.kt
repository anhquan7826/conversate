package com.anhquan.conversate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anhquan.conversate.data.CacheData
import java.time.Instant
import java.util.Date

@Entity
data class Conversation(
    @PrimaryKey val id: String,
    val people: List<String>,
    @ColumnInfo(name = "latest_message") val latestMessage: String? = null,
    @ColumnInfo(name = "latest_timestamp")
    val latestTimeStamp: Date = Date.from(Instant.now())
) {
    fun getOthersEmail(): String {
        return people.find { it != CacheData.user!!.email }!!
    }
}