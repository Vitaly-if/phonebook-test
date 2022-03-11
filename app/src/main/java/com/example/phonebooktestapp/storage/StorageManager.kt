package com.example.phonebooktestapp.storage

import android.content.Context
import androidx.room.Room
import com.example.phonebooktestapp.models.ContactModel

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

    override suspend fun insert(contact: ContactModel) {
        dBHelper?.getContactsDao?.insert(ContactsTable(0,contact.contactAvatarImg,contact.name,contact.phone,contact.category))
    }

    override suspend fun update(contact: ContactModel) {
        dBHelper?.getContactsDao?.update(ContactsTable(contact.contactId,contact.contactAvatarImg,contact.name,contact.phone,contact.category))
    }

    override suspend fun delete(contact: ContactModel) {
        dBHelper?.getContactsDao?.delete(ContactsTable(contact.contactId,contact.contactAvatarImg,contact.name,contact.phone,contact.category))
    }

    override suspend fun getContacts(): List<ContactModel>? {
        val result = dBHelper?.getContactsDao?.getContacts()
        return result?.let { convertList(it) }
    }

    private fun convertList(result: List<ContactsTable>):List<ContactModel> {
        val newResult = mutableListOf<ContactModel>()
        if (result != null) {
            for (it in result) {
                newResult.add(ContactModel(it.contactId,it.contactAvatarImg,it.name,it.phone,it.category))
            }
        }
        return newResult
    }

    // override suspend fun getToContact(): ContactModel? =
   //    dBHelper?.getContactsDao?.getToContact()

    override suspend fun searchDataBase(searchQuery: String): List<ContactModel>? =
        dBHelper?.getContactsDao?.searchDataBase(searchQuery)?.let { convertList(it) }

    override suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactModel>? =
        dBHelper?.getContactsDao?.searchDataBaseForCategory(searchQuery)?.let { convertList(it) }


}