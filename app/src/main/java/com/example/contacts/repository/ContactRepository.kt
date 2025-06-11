package com.example.contacts.repository

import com.example.contacts.room.ContactDatabase
import com.example.contacts.room.Contacts
import com.example.contacts.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContactRepository(private val database : ContactDatabase) {

    suspend fun insertContact(contact : Contacts) : ResultState<Boolean>{
        return try {
            database.getDao().insertContact(contact)
            ResultState.Success(data = true)
        }catch (e : Exception){
            ResultState.Error(error = e.message ?: "Contact addition failed")
        }
    }

    suspend fun updateContact(contact : Contacts) : ResultState<Boolean>{
        return try {
            database.getDao().updateContact(contact)
            ResultState.Success(data = true)
        }catch (e : Exception){
            ResultState.Error(error = e.message ?: "Contact update failed")
        }
    }

    suspend fun deleteContact(contact : Contacts) : ResultState<Boolean>{
        return try{
            database.getDao().deleteContact(contact)
            ResultState.Success(data = true)
        }catch (e : Exception){
            ResultState.Error(error = e.message ?: "Contact deletion failed")
        }
    }

    fun getContacts() : Flow<ResultState<List<Contacts>>> = flow {
        emit(ResultState.Loading)
        try {
            database.getDao().getContacts().collect{
                emit(ResultState.Success(it))
            }
        }catch (e : Exception){
            emit(ResultState.Error(error = e.message ?: "Failed to fetch contacts"))
        }
    }

    suspend fun getContactByNumber(number: String): ResultState<Contacts?> {
        return try {
            val contact = database.getDao().getContactByNumber(number)
            ResultState.Success(contact)
        } catch (e: Exception) {
            ResultState.Error(error = e.message ?: "Failed to fetch contact")
        }
    }


}