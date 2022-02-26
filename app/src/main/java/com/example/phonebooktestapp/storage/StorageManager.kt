package com.example.phonebooktestapp.storage

import android.content.Context
import androidx.room.Room

class StorageManager : IStorageManager {

    private var dBHelper: ContactDatabase? = null
    private lateinit var mContext: Context

    constructor(context: Context) {
        mContext = context

        dBHelper =
            Room.databaseBuilder(context, ContactDatabase::class.java, "contacts_history_database")
                .allowMainThreadQueries()
                .build()
    }

    companion object {

        @Volatile
        private var INSTANCE: StorageManager? = null

        fun getInstance(context: Context): StorageManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildStorage(context).also { INSTANCE = it }
            }

        private fun buildStorage(context: Context) =
            StorageManager(context)
    }

    override suspend fun insert(contactsTable: ContactsTable) {
        dBHelper?.getContactsDao?.insert(contactsTable)
    }

    override suspend fun update(contactsTable: ContactsTable) {
        dBHelper?.getContactsDao?.update(contactsTable)
    }

    override suspend fun delete(contactsTable: ContactsTable) {
        dBHelper?.getContactsDao?.delete(contactsTable)
    }

    override suspend fun getContacts(): List<ContactsTable>? =
        dBHelper?.getContactsDao?.getContacts()

    override suspend fun getToContact(): ContactsTable? =
        dBHelper?.getContactsDao?.getToContact()

    override suspend fun searchDataBase(searchQuery: String): List<ContactsTable>? =
        dBHelper?.getContactsDao?.searchDataBase(searchQuery)

    override suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactsTable>? =
        dBHelper?.getContactsDao?.searchDataBaseForCategory(searchQuery)


}