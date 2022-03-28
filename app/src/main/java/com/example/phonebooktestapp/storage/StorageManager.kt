package com.example.phonebooktestapp.storage

import android.content.Context
import androidx.room.Room
import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.models.ContactsGroupModel
import com.example.phonebooktestapp.storage.table.ContactsTable
import com.example.phonebooktestapp.storage.table.GroupsTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorageManager : IStorageManager {

    private var dBHelper: ContactDatabase? = null
    private lateinit var mContext: Context

    constructor(context: Context) {
        mContext = context

        dBHelper =
            Room.databaseBuilder(context, ContactDatabase::class.java, "contacts_history_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
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

    override suspend fun insert(contact: ContactModel) {
        dBHelper?.getContactsDao?.insert(ContactsTable(0,contact.contactAvatarImg,contact.name,contact.phone,0L))
    }

    override suspend fun update(contact: ContactModel) {
        dBHelper?.getContactsDao?.update(ContactsTable(contact.contactId,contact.contactAvatarImg,contact.name,contact.phone,0L))
    }

    override suspend fun delete(contact: ContactModel) {
        dBHelper?.getContactsDao?.delete(ContactsTable(contact.contactId,contact.contactAvatarImg,contact.name,contact.phone,0L))
    }
    override suspend fun deleteAllGroup() {
        dBHelper?.getContactsDao?.deleteAllGroup()
    }
    override suspend fun getContacts(): List<ContactModel>? {
        val result = dBHelper?.getContactsDao?.getContacts()
        return result?.let { convertListContact(it) }
    }

    override suspend fun searchDataBase(searchQuery: String): List<ContactModel>? =
        dBHelper?.getContactsDao?.searchDataBase(searchQuery)?.let { convertListContact(it) }

    override suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactModel>? =
        dBHelper?.getContactsDao?.searchDataBaseForCategory(searchQuery)?.let { convertListContact(it) }

    override suspend fun addGroup(group: ContactsGroupModel) {
        dBHelper?.getContactsDao?.addGroup(GroupsTable(group.id,group.name))
    }

    override suspend fun getAllGroups(): List<ContactsGroupModel>? {
        val result = dBHelper?.getContactsDao?.getAllGroups()
            return  result?.map { ContactsGroupModel(it.id, it.name) }
    }

    override suspend fun getGroupsForContact(contactID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateGroups(contactID: String, groups: Array<String>) {
        TODO("Not yet implemented")
    }

    private fun convertListContact(result: List<ContactsTable>):List<ContactModel> {
        var newResult = listOf<ContactModel>()
        if (result != null) {
            newResult = result.map{ContactModel(it.contactId, it.contactAvatarImg, it.name, it.phone,it.groupID)}
        }
        return newResult
    }


}