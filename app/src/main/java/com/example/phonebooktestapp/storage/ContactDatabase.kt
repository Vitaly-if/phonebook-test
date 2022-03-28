package com.example.phonebooktestapp.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.phonebooktestapp.storage.dao.ContactsDao
import com.example.phonebooktestapp.storage.table.ContactsTable
import com.example.phonebooktestapp.storage.table.GroupsTable

@Database(entities = [ContactsTable::class, GroupsTable::class], version = 3, exportSchema = false)
abstract class ContactDatabase: RoomDatabase() {
    abstract val getContactsDao: ContactsDao
}