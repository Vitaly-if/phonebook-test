package com.example.phonebooktestapp.ui.home

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.phonebooktestapp.storage.ContactsTable
import com.example.phonebooktestapp.storage.ContactDatabase.Companion.getInstance
import com.example.phonebooktestapp.storage.StorageManager
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getInstance(application)

    private val storageManager = StorageManager(database.contactsDao)

    //live data для загрузки с базы данных
    private val _listContact = MutableLiveData<List<ContactsTable>>()

    val listContactsTable: LiveData<List<ContactsTable>>
        get() = _listContact

    //live data для передачи в detailfragment
    private val _navigateToContact = MutableLiveData<ContactsTable?>()

    //live data для статуса перехода в deteilfragment
    val navigateToSelectedContactsTable: MutableLiveData<ContactsTable?>
        get() = _navigateToContact

    //live data для открытия и заполнения drawermenu
    private val _opendrawermenu = MutableLiveData<Boolean>()
    val opendrawermenu: LiveData<Boolean>
        get() = _opendrawermenu

    init {
        loadAllContacts()
        _opendrawermenu.value = false
    }

    //получение списка контактов из базы данных
    private fun loadAllContacts() {
        viewModelScope.launch {
            try {
                _listContact.value = storageManager.getContacts()

            } catch (e: Exception) {

            }
        }
    }

    //обновление списка контактов
    fun updateContactList() {
        loadAllContacts()
    }

    //поиск контактов по имены
    fun searhContactlist(searchQuery: String) {
        val searchQueryFormat = "%$searchQuery%"
        viewModelScope.launch {
            try {
                _listContact.value = storageManager.searchDataBase(searchQueryFormat)
                Log.i(ContentValues.TAG, "Searchdatabase[0].name=" + _listContact.value!![0].name)

            } catch (e: Exception) {

            }
        }
    }

    //поиск контактов по категориии
    fun filterContactlist(searchQuery: String) {
        val searchQueryFormat = "%$searchQuery%"
        when (searchQuery) {
            "Все контакты" -> {
                loadAllContacts()
            }
            "О программе" -> {

            }
            else -> {
                viewModelScope.launch {
                    try {
                        _listContact.value =
                            storageManager.searchDataBaseForCategory(searchQueryFormat)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    //функция получает в параметре выбранный контакт и изменяет LiveData, далее происходит переход по навигации
    fun displayContactDetails(contactsTable: ContactsTable) {
        _navigateToContact.value = contactsTable
    }

    fun displayDetailsComplete() {
        _navigateToContact.value = null
    }

    //открытие бокового меню начало
    fun openDrawerStart() {
        _opendrawermenu.value = true
    }

    //закрытие бокового меню
    fun openDrawerComplete() {
        _opendrawermenu.value = false
        Log.i(ContentValues.TAG, "Закрывается Drawer")
    }

}

