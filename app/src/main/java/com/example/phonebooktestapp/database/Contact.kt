package com.example.phonebooktestapp.database

import android.os.Parcelable
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "contact_table")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    var contactId: Long = 0L,

    @ColumnInfo(name = "avatar")
    var contactAvatarImg: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "phone")
    var phone: String = "",

    @ColumnInfo(name = "category")
    var category: String = ""

): Parcelable
