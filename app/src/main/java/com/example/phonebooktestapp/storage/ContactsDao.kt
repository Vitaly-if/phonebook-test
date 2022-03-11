package com.example.phonebooktestapp.storage

import androidx.room.*

@Dao
interface ContactsDao {

    @Insert
    suspend fun insert(contactsTable: ContactsTable)

    @Update
    suspend fun update(contactsTable: ContactsTable)

    @Delete
    suspend fun delete(contactsTable: ContactsTable)

    @Query("SELECT * FROM contact_table")
    suspend fun getContacts(): List<ContactsTable>

    //@Query("SELECT * FROM contact_table ORDER BY contactId DESC LIMIT 1")
    //suspend fun getToContact(): ContactsTable?

    @Query("SELECT * FROM contact_table WHERE name LIKE :searchQuery")
    suspend fun searchDataBase(searchQuery: String): List<ContactsTable>

    @Query("SELECT * FROM contact_table WHERE category LIKE :searchQuery")
    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactsTable>


}