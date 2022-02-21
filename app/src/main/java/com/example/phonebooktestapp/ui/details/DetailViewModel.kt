package com.example.phonebooktestapp.ui.details

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.phonebooktestapp.storage.Contact
import com.example.phonebooktestapp.storage.ContactDatabase
import kotlinx.coroutines.launch

class DetailViewModel(contact: Contact?, app: Application) : AndroidViewModel(app) {

    private val database = ContactDatabase.getInstance(app)

    //live data для выбранного контакта
    private val _selectedContact = MutableLiveData<Contact?>()
    val selectedContact: MutableLiveData<Contact?>
        get() = _selectedContact

    //live data для сохранения контакта
    private val _saveContact = MutableLiveData<Boolean>()
    val saveContact: LiveData<Boolean>
        get() = _saveContact

    //live data для закрытия фрагмента
    private var _closeDetilFragment = MutableLiveData<Boolean>()
    val closeDetilFragment: LiveData<Boolean>
        get() = _closeDetilFragment

    //live data для картинки(аватара)
    private var _avatarImgString = MutableLiveData<String>()
    val avatarImgString: LiveData<String>
        get() = _avatarImgString

    //пеерменная для отслеживания новый контакт или нет(используется при сохранении изменений(false)
    //создание нового(true)
    var newContact = true

    //переменная для выбора категории контакта, для указания в RadioButton при открытии DetailFragment
    var selectContactGroupRB = 0


    init {
        //проверка, если приходит null создается пустой контакт
        if (contact == null)
            _selectedContact.value = Contact(0L, "", "")
        else {
            _selectedContact.value = contact
            _avatarImgString.value = contact.contactAvatarImg
            newContact = false
        }
        selectContactGroupRB = getGroupContact(_selectedContact)

    }

    //выбор категории для RadioButton
    private fun getGroupContact(_selectedContact: MutableLiveData<Contact?>): Int {
        var selectCategory = 0
        selectCategory = when (_selectedContact.value?.category) {

            "Друзья" -> 1
            "Коллеги" -> 2
            "Знакомые" -> 3
            "Сокурсники" -> 4
            else -> 0

        }
        return selectCategory
    }
    //добавление контакта в базу данных
    fun insertContact(contact: Contact) {
        viewModelScope.launch {
            database.contactDatabaseDao.insert(contact)
            _closeDetilFragment.value = true
        }
    }

    //обновление контакта в базе данных
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            database.contactDatabaseDao.update(contact)
            _closeDetilFragment.value = true
        }
    }
    //удаление контакта
    fun deleteContact() {
        if (!newContact) {
            viewModelScope.launch {
                _selectedContact.value?.let {
                    database.contactDatabaseDao.delete(it)
                    _closeDetilFragment.value = true
                }
            }
        }
    }
    //сохранение контакта начало
    fun saveContactStart() {
        _saveContact.value = true
    }
    //сохранение контакта завершение
    fun saveContactComplete() {
        _saveContact.value = false
        _closeDetilFragment.value = true
    }

    //добавление аватара
    fun addImage(uri: Uri?) {
        _avatarImgString.value = uri?.toString()
    }
    //обновление аватара
    fun updateImage() {
        Log.i(ContentValues.TAG, "Проверка логов avatarimg = ${_avatarImgString.value}")
        _avatarImgString.value = _avatarImgString.value

    }

}