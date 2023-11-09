package com.anhquan.conversate.data

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.anhquan.conversate.constants.UpdateType
import com.anhquan.conversate.helper.IdGenerator
import com.anhquan.conversate.model.Conversation
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.Date

interface ConversationRepository {
    suspend fun getConversations(): MutableList<Conversation>

    suspend fun getConversationByID(id: String): Conversation?

    suspend fun getConversationByContact(email: String): Conversation

    fun generateNewConversation(email: String): Conversation

    suspend fun setNewConversation(conversation: Conversation)

    suspend fun conversationsListener(listener: (conversation: Conversation, type: UpdateType) -> Unit)

    suspend fun cancelListener()
}

@Suppress("UNCHECKED_CAST")
class ConversationRepositoryImpl : ConversationRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private var subscriber: ListenerRegistration? = null

    override suspend fun getConversations(): MutableList<Conversation> {
        val query = firestore.collection("/CONVERSATIONS").whereArrayContains(
            "people", CacheData.user!!.email
        ).get().await()
        val result = mutableListOf<Conversation>()
        for (doc in query) {
            result.add(
                Conversation(
                    id = doc.id,
                    people = doc.get("people") as List<String>,
                    latestMessage = if (doc.get("latestMessage") == null) null else doc.get("latestMessage")
                        .toString()
                )
            )
        }
        return result
    }

    override suspend fun getConversationByID(id: String): Conversation? {
        val doc = firestore.document("/CONVERSATIONS/$id").get().await()
        return if (doc.exists()) {
            Conversation(
                id = doc.id,
                people = doc.get("people") as List<String>,
                latestMessage = if (doc.get("latestMessage") == null) null else doc.get("latestMessage")
                    .toString()
            )
        } else {
            null
        }
    }

    override suspend fun getConversationByContact(email: String): Conversation {
        val ref = firestore.collection("/CONVERSATIONS")
        var query = ref.whereEqualTo(
            "people", listOf(email, CacheData.user!!.email),
        ).get().await()
        if (query.isEmpty) {
            query = ref.whereEqualTo(
                "people", listOf(CacheData.user!!.email, email)
            ).get().await()
        }
        return Conversation(
            id = query.first().id,
            people = query.first().get("people") as List<String>,
            latestMessage = query.first().get("latestMessage")?.toString(),
            latestTimeStamp = query.first().getTimestamp("latestTimeStamp")?.toDate() ?: Date.from(
                Instant.now()
            )
        )

    }

    override fun generateNewConversation(email: String): Conversation {
        return Conversation(
            id = IdGenerator.generateDocumentId(),
            people = listOf(CacheData.user!!.email, email),
        )
    }

    override suspend fun setNewConversation(conversation: Conversation) {
        firestore.document("/CONVERSATIONS/${conversation.id}").set(
            hashMapOf(
                "people" to conversation.people
            )
        )
    }

    override suspend fun conversationsListener(listener: (conversation: Conversation, type: UpdateType) -> Unit) {
        subscriber = firestore.collection("/CONVERSATIONS").whereArrayContains(
            "people", CacheData.user!!.email
        ).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documentChanges) {
                    if (doc.document.get("latestMessage") != null) {
                        listener(
                            Conversation(
                                id = doc.document.id,
                                people = doc.document.get("people") as List<String>,
                                latestMessage = doc.document.get("latestMessage")?.toString(),
                                latestTimeStamp = doc.document.getTimestamp("timeStamp")?.toDate()
                                    ?: Date.from(
                                        Instant.now()
                                    )
                            ), when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    UpdateType.Added
                                }

                                DocumentChange.Type.REMOVED -> {
                                    UpdateType.Deleted
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    UpdateType.Changed
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override suspend fun cancelListener() {
        subscriber?.remove()
        subscriber = null
    }
}