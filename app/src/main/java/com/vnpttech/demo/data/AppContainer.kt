package com.vnpttech.demo.data

import javax.inject.Singleton

interface AppContainer {
    val cache: CacheData
    val users: UserRepository
    val conversations: ConversationRepository
    val contacts: ContactRepository
    val messages: MessageRepository
}

@Singleton
class AppContainerImpl : AppContainer {
    override val cache: CacheData = CacheData
    override val users: UserRepository = UserRepositoryImpl()
    override val conversations: ConversationRepository = ConversationRepositoryImpl()
    override val contacts: ContactRepository = ContactRepositoryImpl()
    override val messages: MessageRepository = MessageRepositoryImpl()
}