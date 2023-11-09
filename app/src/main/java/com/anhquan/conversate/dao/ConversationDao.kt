package com.anhquan.conversate.dao

import androidx.room.Dao
import androidx.room.Query
import com.anhquan.conversate.model.Conversation

@Dao
interface ConversationDao {
    @Query("select * from conversation")
    suspend fun getConversations(): MutableList<Conversation>

    @Query("select * from conversation where id = :id")
    suspend fun getConversationByID(id: String): Conversation?

    @Query("select * from conversation where people like '%' || :email || '%'")
    suspend fun getConversationByContact(email: String): Conversation
}