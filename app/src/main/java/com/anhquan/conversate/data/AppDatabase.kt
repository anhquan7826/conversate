package com.anhquan.conversate.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anhquan.conversate.dao.ConversationDao
import com.anhquan.conversate.dao.MessageDao
import com.anhquan.conversate.dao.UserDao
import com.anhquan.conversate.helper.DateConverter
import com.anhquan.conversate.helper.ListConverter
import com.anhquan.conversate.model.Conversation
import com.anhquan.conversate.model.Message
import com.anhquan.conversate.model.User

@Database(
    entities = [User::class, Message::class, Conversation::class], version = 1
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
}

        class DatabaseHelper(context: Context)
            : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
            companion object {
                const val DATABASE_NAME = "test_db"
                const val DATABASE_VERSION = 1
            }

            override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL("")
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                TODO("Not yet implemented")
            }
        }

@SuppressLint("Recycle")
@Composable
fun test() {
    val context = LocalContext.current

    val db = DatabaseHelper(context)
    val dbReader = db.readableDatabase

    val cursor = dbReader.query(
        "person", // Table name
        arrayOf("id", "name", "age", "city"), // Columns to get
        "age > ?, city = ?", // WHERE clause
        arrayOf("20", "Hanoi"), // Values for WHERE clause
        null, // GROUP BY clause
        null, // HAVING clause
        "name asc" // SORT BY clause
    )

    while (cursor.moveToNext()) {
        val personID = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
        val personName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
    }

    val dbWriter = db.writableDatabase

    dbWriter.insert(
        "person",
        null,
        ContentValues().apply {
            put("id", 999)
            put("name", "bob")
            put("age", 32)
            put("city", "New York")
        }
    )


}