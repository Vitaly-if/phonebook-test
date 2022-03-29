package com.example.phonebooktestapp.storage.dao

import androidx.room.*
import com.example.phonebooktestapp.models.ContactsGroupModel
import com.example.phonebooktestapp.storage.table.ContactsTable
import com.example.phonebooktestapp.storage.table.GroupsTable
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM contact_table WHERE name LIKE :searchQuery")
    suspend fun searchDataBase(searchQuery: String): List<ContactsTable>

    @Query("SELECT * FROM contact_table WHERE contact_table.group_id LIKE :searchQuery")
    suspend fun searchDataBaseForCategory(searchQuery: String): List<ContactsTable>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    suspend fun addGroup(group: GroupsTable)

    @Query("SELECT * FROM group_table")
    suspend fun getAllGroups(): List<GroupsTable>

    @Query("DELETE FROM group_table")
    suspend fun deleteAllGroup()


}