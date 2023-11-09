package com.anhquan.conversate.dao

import androidx.room.Dao
import androidx.room.Query
import com.anhquan.conversate.model.Message

@Dao
interface MessageDao {
    @Query("select * from message where id = :conversationID")
    suspend fun getAllMessages(conversationID: String): MutableList<Message>
}