package com.example.contacts.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Contacts::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun getDao(): ContactDao

    companion object {
        @Volatile
        private var Instance: ContactDatabase? = null

        fun getDatabase(context: Context): ContactDatabase {
            if (Instance == null) {
                synchronized(this) {
                    if (Instance == null) {
                        Instance = Room.databaseBuilder(
                            context.applicationContext,
                            ContactDatabase::class.java,
                            "ContactDb"
                        ).build()
                    }
                }
            }
            return Instance!!
        }

    }
}