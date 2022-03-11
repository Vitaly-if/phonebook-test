package com.example.phonebooktestapp.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.storage.ContactsTable

class DetailViewModelFactory(
    private val contactsTable: ContactModel?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(contactsTable, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}