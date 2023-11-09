package com.anhquan.conversate.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.anhquan.conversate.model.Message
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.Date

interface MessageRepository {
    suspend fun getAllMessages(conversationID: String): MutableList<Message>
    suspend fun sendMessage(conversationID: String, message: String)
    suspend fun messageListener(conversationID: String, listener: (Message) -> Unit)
    suspend fun cancelListener()
}

class MessageRepositoryImpl : MessageRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private var subscriber: ListenerRegistration? = null

    override suspend fun getAllMessages(conversationID: String): MutableList<Message> {
        val query =
            firestore.collection("/CONVERSATIONS/$conversationID/messages").orderBy("timeStamp")
                .get().await()
        val result = mutableListOf<Message>()
        for (doc in query) {
            result.add(
                Message(
                    id = doc.id,
                    sender = doc.get("sender").toString(),
                    content = doc.get("content").toString(),
                    timeStamp = (doc.get("timeStamp") as Timestamp).toDate()
                )
            )
        }
        return result
    }

    override suspend fun sendMessage(conversationID: String, message: String) {
        firestore.collection("/CONVERSATIONS/$conversationID/messages").add(
            hashMapOf(
                "content" to message,
                "sender" to CacheData.user!!.email,
                "timeStamp" to FieldValue.serverTimestamp()
            )
        )
        firestore.document("/CONVERSATIONS/$conversationID").update(
            "latestMessage", message, "latestTimeStamp", FieldValue.serverTimestamp()
        )
    }

    override suspend fun messageListener(conversationID: String, listener: (Message) -> Unit) {
        subscriber =
            firestore.collection("/CONVERSATIONS/$conversationID/messages").orderBy("timeStamp")
                .addSnapshotListener { value, _ ->
                    if (value != null) {
                        for (doc in value.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED) {
                                listener(
                                    Message(
                                        id = doc.document.id,
                                        sender = doc.document.get("sender").toString(),
                                        content = doc.document.get("content").toString(),
                                        timeStamp = doc.document.getTimestamp("timeStamp")?.toDate()
                                            ?: Date.from(
                                                Instant.now()
                                            )
                                    )
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