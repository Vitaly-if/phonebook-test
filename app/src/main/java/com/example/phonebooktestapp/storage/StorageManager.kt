package com.example.phonebooktestapp.storage

class StorageManager(private val ContactsDao: ContactsDao) {

    suspend fun insert(contactsTable: ContactsTable) {
        ContactsDao.insert(contactsTable)
    }
    suspend fun update(contactsTable: ContactsTable) {
        ContactsDao.update(contactsTable)
    }
    suspend fun delete(contactsTable: ContactsTable) {
        ContactsDao.delete(contactsTable)
    }
    suspend fun getContacts(): List<ContactsTable> {
        return ContactsDao.getContacts()
    }
    suspend fun getToContact(): ContactsTable? {
        return ContactsDao.getToContact()
    }
    suspend fun searchDataBase(searchQuery: String): List<ContactsTable> {
        return ContactsDao.searchDataBase(searchQuery)
    }
    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactsTable> {
        return ContactsDao.searchDataBaseForCategory(searchQuery)
    }


}