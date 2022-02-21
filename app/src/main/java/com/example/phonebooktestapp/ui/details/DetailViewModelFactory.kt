package com.example.phonebooktestapp.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.phonebooktestapp.storage.Contact

class DetailViewModelFactory(
    private val contact: Contact?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(contact, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}