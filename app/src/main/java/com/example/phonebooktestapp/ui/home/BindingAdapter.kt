package com.example.phonebooktestapp.ui.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebooktestapp.storage.ContactsTable

//адаптер связывающий viewModel.listContactsTable и RecyclerView фрагмента
@BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<ContactsTable>?) {
        val adapter = recyclerView.adapter as ContactLinearAdapter
        adapter.submitList(data)
    }
