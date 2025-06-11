package com.example.contacts.contactlogs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Logs(
    val id : String,
    val name : String?,
    val number: String,
    val iconRes : Int
) : Parcelable
