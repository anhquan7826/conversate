package com.anhquan.conversate.data

import com.google.firebase.firestore.FirebaseFirestore
import com.anhquan.conversate.model.ContactRequest
import com.anhquan.conversate.model.User
import kotlinx.coroutines.tasks.await

interface ContactRepository {
    suspend fun getContacts(): List<User>

    suspend fun getContactRequests(): List<ContactRequest>

    suspend fun requestsAvailable(): Boolean

    suspend fun contactRequestOperation(email: String, accept: Boolean)

    suspend fun sendContactRequest(email: String, requestMessage: String)

    suspend fun removeContact(email: String): Boolean
}

class ContactRepositoryImpl : ContactRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getContacts(): List<User> {
        val query = firestore.collection("/USERS/${CacheData.user!!.email}/contacts").get().await()
        val result = mutableListOf<User>()
        for (q in query) {
            result.add(
                getUserFromEmail(q.id)
            )
        }
        result.sortBy {
            it.displayName
        }
        return result
    }

    override suspend fun getContactRequests(): List<ContactRequest> {
        val query =
            firestore.collection("/USERS/${CacheData.user!!.email}/contact_requests").get().await()
        val requests = mutableListOf<ContactRequest>()
        for (doc in query) {
            requests.add(
                ContactRequest(
                    user = getUserFromEmail(doc.id),
                    requestMessage = doc.data["requestMessage"]!!.toString()
                )
            )
        }
        return requests
    }

    override suspend fun requestsAvailable(): Boolean {
        val query =
            firestore.collection("/USERS/${CacheData.user!!.email}/contact_requests").get().await()
        return !query.isEmpty
    }

    override suspend fun contactRequestOperation(email: String, accept: Boolean) {
        firestore.document("/USERS/${CacheData.user!!.email}/contact_requests/$email").delete()
        if (accept) {
            firestore.document("/USERS/${CacheData.user!!.email}/contacts/$email").set(
                hashMapOf("added" to true)
            )
            firestore.document("/USERS/$email/contacts/${CacheData.user!!.email}").set(
                hashMapOf("added" to true)
            )
            firestore.collection("/CONVERSATIONS").add(
                hashMapOf(
                    "people" to listOf(CacheData.user!!.email, email)
                )
            )
        }
    }

    override suspend fun sendContactRequest(email: String, requestMessage: String) {
        firestore.document("/USERS/$email/contact_requests/${CacheData.user!!.email}").set(
            hashMapOf("requestMessage" to requestMessage)
        ).await()
    }

    override suspend fun removeContact(email: String): Boolean {
        firestore.document("/USERS/${CacheData.user!!.email}/contacts/$email").delete()
        firestore.document("/USERS/$email/contacts/${CacheData.user!!.email}").delete()
        val collectionRef = firestore.collection("/CONVERSATIONS")
        var docRef = collectionRef.whereEqualTo(
            "people", listOf(CacheData.user!!.email, email)
        ).get().await()
        if (docRef.isEmpty) {
            docRef = collectionRef.whereEqualTo(
                "people", listOf(email, CacheData.user!!.email)
            ).get().await()
        }
        for (doc in docRef) {
            doc.reference.collection("messages").get().addOnSuccessListener {
                for (subdoc in it) {
                    subdoc.reference.delete()
                }
            }
            firestore.document("/CONVERSATIONS/${doc.id}").delete()
        }
        return true
    }

    private suspend fun getUserFromEmail(email: String): User {
        val docRef = firestore.document("/USERS/${email}").get().await()
        return User(
            email = email,
            avatar = docRef.data!!["avatar"].toString(),
            displayName = docRef.data!!["displayName"].toString()
        )
    }
}