package com.anhquan.conversate.data

import android.content.Context
import androidx.room.Room
import javax.inject.Singleton

interface AppContainer {
    val cache: CacheData
    val users: UserRepository
    val conversations: ConversationRepository
    val contacts: ContactRepository
    val messages: MessageRepository
    var database: AppDatabase

    fun initDatabase(context: Context)
}

@Singleton
class AppContainerImpl : AppContainer {
    override val cache: CacheData = CacheData
    override val users: UserRepository = UserRepositoryImpl()
    override val conversations: ConversationRepository = ConversationRepositoryImpl()
    override val contacts: ContactRepository = ContactRepositoryImpl()
    override val messages: MessageRepository = MessageRepositoryImpl()

    override lateinit var database: AppDatabase

    override fun initDatabase(context: Context) {
        database = Room.databaseBuilder(
            context, AppDatabase::class.java, "demo_internship",
        ).build()
    }
}