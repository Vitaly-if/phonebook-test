package com.example.phonebooktestapp.storage

import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.models.ContactsGroupModel
import kotlinx.coroutines.flow.Flow

interface IStorageManager {

    suspend fun insert(contactsTable: ContactModel)

    suspend fun update(contactsTable: ContactModel)

    suspend fun delete(contactsTable: ContactModel)

    suspend fun getContacts(): List<ContactModel>?

    suspend fun searchDataBase(searchQuery: String): List<ContactModel>?

    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactModel>?
    // Groups
    suspend fun addGroup(groupModel: ContactsGroupModel)

    suspend fun getAllGroups(): List<ContactsGroupModel>?

    suspend fun deleteAllGroup()

    suspend fun getGroupsForContact(contactID: String)

    suspend fun updateGroups(contactID: String, groups: Array<String>)

}