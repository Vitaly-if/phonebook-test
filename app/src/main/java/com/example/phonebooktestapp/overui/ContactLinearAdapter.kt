package com.example.phonebooktestapp.overui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebooktestapp.database.Contact
import com.example.phonebooktestapp.databinding.ListItemBinding
import com.bumptech.glide.Glide

//класс реализует адаптер RecyclerView ListAdapter, который использует привязку данных для представления List, данные включаю различия между списками
//в параметре принимается лямбда слушателя нажатия
class ContactLinearAdapter(val clickListener: ContactListener) :
    ListAdapter<Contact, ContactLinearAdapter.ViewHolder>(ContactDiffCallback()) {

    //создание новых представлений элементов RecyclerView (вызывается менеджером компоновки)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    //конструктор ViewHolder берет переменную привязки из ListItemBinding, который обеспечивает доступ к информации
    class ViewHolder private constructor(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ContactListener, item: Contact) {
            binding.contact = item
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
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    //кастомный слушатель обрабатывает клики по элементам RecyclerView, проходит Contact связанный с текущим элементом в
    //функцию onClick, clickListener лямбда которая будет вызываться с текущим Contact
    class ContactListener(val clickListener: (contact: Contact) -> Unit) {
        fun onClick(contact: Contact) = clickListener(contact)
    }

    //Заменяет содержимое представления (вызывается менеджером компоновки)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position) as Contact
        holder.bind(clickListener, contact)
    }
}
//позволяет RecyclerView определять, какие элементы были изменены, когда список был обновлен
class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}


