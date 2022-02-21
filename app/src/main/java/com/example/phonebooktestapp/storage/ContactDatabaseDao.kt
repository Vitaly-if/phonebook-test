package com.example.phonebooktestapp.storage

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDatabaseDao {

    @Insert
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contact_table")
    suspend fun getContacts(): List<Contact>

    @Query("SELECT * FROM contact_table ORDER BY contactId DESC LIMIT 1")
    suspend fun getTocontact(): Contact?

    @Query("SELECT * FROM contact_table WHERE name LIKE :searchQuery")
    suspend fun searchDataBase(searchQuery: String): List<Contact>

    @Query("SELECT * FROM contact_table WHERE category LIKE :searchQuery")
    suspend fun searchDataBaseForCategory(searchQuery: String): List<Contact>


}