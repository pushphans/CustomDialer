package com.example.contacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.repository.ContactRepository
import com.example.contacts.room.Contacts
import com.example.contacts.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _addState = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val addState: StateFlow<ResultState<Boolean>> get() = _addState

    private val _updateState = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val updateState: StateFlow<ResultState<Boolean>> get() = _updateState

    private val _deleteState = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val deleteState: StateFlow<ResultState<Boolean>> get() = _deleteState

    private val _contactState = MutableStateFlow<ResultState<List<Contacts>>>(ResultState.Loading)
    val contactState: StateFlow<ResultState<List<Contacts>>> get() = _contactState

    private val _searchContactState = MutableStateFlow<ResultState<Contacts?>>(ResultState.Loading)
    val searchContactState : StateFlow<ResultState<Contacts?>> get() = _searchContactState


    fun insertContact(contact: Contacts) {
        viewModelScope.launch {
            _addState.value = ResultState.Loading
            val addResult = repository.insertContact(contact)
            _addState.value = addResult
        }
    }

    fun updateContact(contact: Contacts) {
        viewModelScope.launch {
            _updateState.value = ResultState.Loading
            val updateResult = repository.updateContact(contact)
            _updateState.value = updateResult
        }
    }

    fun deleteContact(contact: Contacts) {
        viewModelScope.launch {
            _deleteState.value = ResultState.Loading
            val deleteResult = repository.deleteContact(contact)
            _deleteState.value = deleteResult
        }
    }

    fun getContacts(){
        viewModelScope.launch {
            repository.getContacts().collectLatest {
                _contactState.value = it
            }
        }
    }


    fun getContactByNumber(number: String) {
        viewModelScope.launch {
            _searchContactState.value = ResultState.Loading
            val result = repository.getContactByNumber(number)
            _searchContactState.value = result
        }
    }

    suspend fun getContactByNumberSuspending(number: String): Contacts? {
        return when (val result = repository.getContactByNumber(number)) {
            is ResultState.Success -> result.data
            else -> null
        }
    }

}