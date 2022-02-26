package com.example.phonebooktestapp.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContactsTable::class], version = 2, exportSchema = false)
abstract class ContactDatabase: RoomDatabase() {
    abstract val getContactsDao: ContactsDao
}