package com.vnpttech.demo.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vnpttech.demo.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

interface UserRepository {
    fun isLoggedIn(): Boolean
    suspend fun getUserFromEmail(email: String): User?
    fun setCurrentUserData(user: User)
    fun logout()
}

class UserRepositoryImpl : UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    init {
        val u = Firebase.auth.currentUser
        if (u != null) {
            CacheData.user = User(
                displayName = u.displayName.toString(),
                email = u.email.toString(),
                avatar = u.photoUrl.toString()
            )
        }
    }

    override fun isLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun getUserFromEmail(email: String): User? {
        val docSnapshot = firestore.document("/USERS/$email").get().await()
        val result: User? = if (docSnapshot.data == null) {
            Log.d("print", "user '$email' is null")
            null
        } else User(
            displayName = docSnapshot.data!!["displayName"].toString(),
            avatar = docSnapshot.data!!["avatar"].toString(),
            email = docSnapshot.data!!["email"].toString()
        )
        return result
    }

    override fun setCurrentUserData(user: User) {
        CacheData.user = user
        firestore.document("/USERS/${user.email}").set(
            hashMapOf(
                "displayName" to user.displayName, "email" to user.email, "avatar" to user.avatar
            )
        )
    }

    override fun logout() {
        FirebaseAuth.getInstance().signOut()
        CacheData.user = null
    }
}