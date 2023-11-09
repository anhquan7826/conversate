package com.anhquan.conversate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date

@Entity
data class Message(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "timestamp") val timeStamp: Date = Date.from(Instant.now()),
)