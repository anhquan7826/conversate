package com.anhquan.conversate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

    @Entity
    data class User(
        @ColumnInfo(name = "avatar") val avatar: String?,
        @ColumnInfo(name = "display_name") val displayName: String,
        @PrimaryKey val email: String,
    )