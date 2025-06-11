package com.example.contacts.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Contacts(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    val number : String
) : Parcelable
