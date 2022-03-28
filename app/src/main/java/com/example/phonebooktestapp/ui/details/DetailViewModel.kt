package com.example.phonebooktestapp.ui.details

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.models.ContactsGroupModel
import com.example.phonebooktestapp.storage.StorageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel(contactsTable: ContactModel?, app: Application) : AndroidViewModel(app) {

    private val storageManager = StorageManager.getInstance(app)

    //live data для выбранного контакта
    private val _selectedContact = MutableLiveData<ContactModel?>()
    val selectedContactsTable: MutableLiveData<ContactModel?>
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

    private var _listContactsGroup = MutableLiveData<List<ContactsGroupModel>>()
    val listContactsGroup: LiveData<List<ContactsGroupModel>>
        get() = _listContactsGroup

    //пеерменная для отслеживания новый контакт или нет(используется при сохранении изменений(false)
    //создание нового(true)
    var newContact = true

    //переменная для выбора категории контакта, для указания в RadioButton при открытии DetailFragment
    var selectContactGroupRB = 0


    init {
        getContactsGroup()

        //проверка, если приходит null создается пустой контакт
        if (contactsTable == null)
            _selectedContact.value = ContactModel(0L,"","","",0L)
        else {
            _selectedContact.value = contactsTable
            _avatarImgString.value = contactsTable.contactAvatarImg
            newContact = false
        }
        //исправить
    //   selectContactGroupRB = getGroupContact(_selectedContact)

    }

    //выбор категории для RadioButton
    //исправить
//    private fun getGroupContact(_selectedContactsTable: MutableLiveData<ContactModel?>): Int {
//        var selectCategory = 0
//        selectCategory = when (_selectedContactsTable.value?.category) {
//
//            "Друзья" -> 1
//            "Коллеги" -> 2
//            "Знакомые" -> 3
//            "Сокурсники" -> 4
//            else -> 0
//
//        }
//        return selectCategory
//    }
//    //добавление контакта в базу данных
//    fun insertContact(contactsTable: ContactsTable) {
//        viewModelScope.launch {
//            storageManager.insert(contactsTable)
//            _closeDetilFragment.value = true
//        }
//    }
    fun getContactsGroup() {
        viewModelScope.launch {
           _listContactsGroup.value = storageManager.getAllGroups()
        }
    }

    //добавление контакта в базу данных
    fun insertContact(contactsTable: ContactModel) {
        viewModelScope.launch {
            storageManager.insert(contactsTable)
            _closeDetilFragment.value = true
        }
    }

    //обновление контакта в базе данных
    fun updateContact(contactsTable: ContactModel) {
        viewModelScope.launch {
            storageManager.update(contactsTable)
            _closeDetilFragment.value = true
        }
    }
    //удаление контакта
    fun deleteContact() {
        if (!newContact) {
            viewModelScope.launch {
                _selectedContact.value?.let {
                    storageManager.delete(it)
                    _closeDetilFragment.value = true
                }
            }
        }
    }
    fun deleteGroup() {
        viewModelScope.launch {
            storageManager.deleteAllGroup()
            delay(200)
            getContactsGroup()
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

    fun insertGroup(categoryNew: ContactsGroupModel) {
        viewModelScope.launch {
            storageManager.addGroup(categoryNew)
            getContactsGroup()
        }

    }

}