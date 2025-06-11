package com.example.contacts.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact : Contacts)

    @Update
    suspend fun updateContact(contact: Contacts)

    @Delete
    suspend fun deleteContact(contact: Contacts)

    @Query("SELECT * FROM contacts ORDER BY id DESC")
    fun getContacts() : Flow<List<Contacts>>

    @Query("SELECT * FROM contacts WHERE number = :number LIMIT 1")
    suspend fun getContactByNumber(number: String): Contacts?

//    @Query("SELECT * FROM contacts WHERE number = :query")
//    fun searchContact(query : String) : Contacts?
}