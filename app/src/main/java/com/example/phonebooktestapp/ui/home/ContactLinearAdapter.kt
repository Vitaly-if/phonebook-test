package com.example.phonebooktestapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebooktestapp.databinding.ContactListItemBinding
import com.bumptech.glide.Glide
import com.example.phonebooktestapp.models.ContactModel

//класс реализует адаптер RecyclerView ListAdapter, который использует привязку данных для представления List, данные включаю различия между списками
//в параметре принимается лямбда слушателя нажатия
class ContactLinearAdapter(val clickListener: ContactListener) :
    ListAdapter<ContactModel, ContactLinearAdapter.ViewHolder>(ContactDiffCallback()) {

    //создание новых представлений элементов RecyclerView (вызывается менеджером компоновки)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    //конструктор ViewHolder берет переменную привязки из ListItemBinding, который обеспечивает доступ к информации
    class ViewHolder private constructor(val binding: ContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ContactListener, item: ContactModel) {
            binding.contactModel = item
            binding.chapterName.text = item.name
            Glide.with(itemView.context)
                .load(item.contactAvatarImg) // Uri of the picture
                .circleCrop()
                .into(binding.contactImage)
            binding.clickListener = clickListener
            //поскольку привязка выполняется немедленно, позволяет RecyclerView правильно измерять размер представления/
            //то есть это отметка привязки что что то изменилось
            binding.executePendingBindings()

        }

        //создание элемента держателя
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContactListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    //кастомный слушатель обрабатывает клики по элементам RecyclerView, проходит ContactsTable связанный с текущим элементом в
    //функцию onClick, clickListener лямбда которая будет вызываться с текущим ContactsTable
    class ContactListener(val clickListener: (contactsTable: ContactModel) -> Unit) {
        fun onClick(contactsTable: ContactModel) = clickListener(contactsTable)
    }

    //Заменяет содержимое представления (вызывается менеджером компоновки)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position) as ContactModel
        holder.bind(clickListener, contact)
    }
}
//позволяет RecyclerView определять, какие элементы были изменены, когда список был обновлен
class ContactDiffCallback : DiffUtil.ItemCallback<ContactModel>() {
    override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return oldItem == newItem
    }
}


