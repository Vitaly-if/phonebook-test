package com.example.phonebooktestapp.overui

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.phonebooktestapp.database.Contact
import com.example.phonebooktestapp.database.ContactDatabase.Companion.getInstance
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getInstance(application)

    //live data для загрузки с базы данных
    private val _listContact = MutableLiveData<List<Contact>>()

    val listContact: LiveData<List<Contact>>
        get() = _listContact

    //live data для передачи в detailfragment
    private val _navigateToContact = MutableLiveData<Contact?>()

    //live data для статуса перехода в deteilfragment
    val navigateToSelectedContact: MutableLiveData<Contact?>
        get() = _navigateToContact

    //live data для открытия и заполнения drawermenu
    private val _opendrawermenu = MutableLiveData<Boolean>()
    val opendrawermenu: LiveData<Boolean>
        get() = _opendrawermenu

    init {
        getContactlist()
    }

    //получение списка контактов из базы данных
    private fun getContactlist() {
        viewModelScope.launch {
            try {
                _listContact.value = database.contactDatabaseDao.getContacts()

            } catch (e: Exception) {

            }
        }
    }

    //обновление списка контактов
    fun updateContactList() {
        getContactlist()
    }

    //поиск контактов по имены
    fun getSearhContactlist(searchQuery: String) {
        val searchQueryFormat = "%$searchQuery%"
        viewModelScope.launch {
            try {
                _listContact.value = database.contactDatabaseDao.searchDataBase(searchQueryFormat)
                Log.i(ContentValues.TAG, "Searchdatabase[0].name=" + _listContact.value!![0].name)

            } catch (e: Exception) {

            }
        }
    }

    //поиск контактов по категориии
    fun getFilterContactlist(searchQuery: String) {
        val searchQueryFormat = "%$searchQuery%"
        when (searchQuery) {
            "Все контакты" -> {
                getContactlist()
            }
            "О программе" -> {

            }
            else -> {
                viewModelScope.launch {
                    try {
                        _listContact.value =
                            database.contactDatabaseDao.searchDataBaseForCategory(searchQueryFormat)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    //функция получает в параметре выбранный контакт и изменяет LiveData, далее происходит переход по навигации
    fun displayContactDetails(contact: Contact) {
        _navigateToContact.value = contact
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

