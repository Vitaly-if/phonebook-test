package com.example.phonebooktestapp.storage

import com.example.phonebooktestapp.models.ContactModel

interface IStorageManager {

    suspend fun insert(contactsTable: ContactModel)

    suspend fun update(contactsTable: ContactModel)

    suspend fun delete(contactsTable: ContactModel)

    suspend fun getContacts(): List<ContactModel>?

   // suspend fun getToContact(): ContactModel?

    suspend fun searchDataBase(searchQuery: String): List<ContactModel>?

    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactModel>?

}