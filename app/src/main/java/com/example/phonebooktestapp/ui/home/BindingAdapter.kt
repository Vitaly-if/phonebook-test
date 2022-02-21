package com.example.phonebooktestapp.ui.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebooktestapp.storage.Contact

//адаптер связывающий viewModel.listContact и RecyclerView фрагмента
@BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<Contact>?) {
        val adapter = recyclerView.adapter as ContactLinearAdapter
        adapter.submitList(data)
    }
