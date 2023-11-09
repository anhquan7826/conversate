package com.anhquan.conversate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.anhquan.conversate.model.User

    @Dao
    interface UserDao {
        @Query("select * from user where email = :email")
        suspend fun getUserFromEmail(email: String): User?

        @Query("SELECT * FROM user")
        fun getAll(): List<User>

        @Insert
        fun insertAll(users: User)

        @Delete
        fun delete(user: User)
    }


