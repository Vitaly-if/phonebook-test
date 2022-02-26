package com.example.phonebooktestapp.storage

interface IStorageManager {

    suspend fun insert(contactsTable: ContactsTable)

    suspend fun update(contactsTable: ContactsTable)

    suspend fun delete(contactsTable: ContactsTable)

    suspend fun getContacts(): List<ContactsTable>?

    suspend fun getToContact(): ContactsTable?

    suspend fun searchDataBase(searchQuery: String): List<ContactsTable>?

    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactsTable>?

}