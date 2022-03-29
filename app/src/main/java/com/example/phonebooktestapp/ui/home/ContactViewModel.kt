package com.example.phonebooktestapp.ui.home

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.models.ContactsGroupModel
import com.example.phonebooktestapp.storage.StorageManager
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = StorageManager.getInstance(application)

    //live data для загрузки с базы данных
    private val _listContact = MutableLiveData<List<ContactModel>>()

    val listContactsTable: LiveData<List<ContactModel>>
        get() = _listContact

    //live data для передачи в detailfragment
    private val _navigateToContact = MutableLiveData<ContactModel?>()

    //live data для статуса перехода в deteilfragment
    val navigateToSelectedContactsTable: MutableLiveData<ContactModel?>
        get() = _navigateToContact

    //live data для открытия и заполнения drawermenu
    private val _opendrawermenu = MutableLiveData<Boolean>()
    val opendrawermenu: LiveData<Boolean>
        get() = _opendrawermenu

    private var _listContactsGroup = MutableLiveData<List<ContactsGroupModel>>()
    val listContactsGroup: LiveData<List<ContactsGroupModel>>
        get() = _listContactsGroup

    init {
        loadAllContacts()
        getContactsGroup()
        _opendrawermenu.value = false
    }

    private fun getContactsGroup() {
        viewModelScope.launch {
            _listContactsGroup.value = storageManager.getAllGroups()
        }
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

    //обновление списка контактов и списка групп
    fun updateContactList() {
        loadAllContacts()
        getContactsGroup()
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
    fun filterContactlist(searchQuery: Long) {
        val searchQueryFormat = "%$searchQuery%"
        when (searchQuery) {
            0L -> {
                loadAllContacts()
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
    fun displayContactDetails(contactsTable: ContactModel) {
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

