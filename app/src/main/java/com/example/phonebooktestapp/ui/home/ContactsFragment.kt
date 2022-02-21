package com.example.phonebooktestapp.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonebooktestapp.R
import com.example.phonebooktestapp.databinding.FragmentContactsListBinding

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactViewModel by lazy {
        ViewModelProvider(this).get(ContactViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        setupBind(inflater,container)

        setupUI()

        bindVM()

        return binding.root
    }

    private fun setupBind(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_contacts_list,
            container,
            false
        )
        //наблюдение привязки данных за LiveData этого фрагмента
        binding.lifecycleOwner = this
        //Предоставление привязки доступа к ContactViewModel
        binding.viewModel = viewModel
    }
    private fun setupUI() {
        setupCategoryList()
        setupAddContactBottom()
        setHasOptionsMenu(true)
    }

    private fun setupCategoryList() {
        //заполнение listview  бокового меню
        val listCategory =
            arrayOf("Все контакты", "Друзья", "Коллеги", "Знакомые", "Сокурсники", "О программе")

        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.category_list_item,
                listCategory
            )
        }
        var categoryListView = binding.categoryListView
        categoryListView.adapter = adapter
    }

    private fun setupAddContactBottom() {
        //переход в пустой фрагмент создание нового контакта
        binding.addContactButton.setOnClickListener {
            this.findNavController().navigate(
                ContactsFragmentDirections.actionContactsFragmentToDetailFragment(null)
            )
        }
    }
    private fun bindVM() {
        //Наблюдатель открытия бокового меню
        viewModel.opendrawermenu.observe(
            this,
            Observer {
                if (it) {
                    val draverLayout = view?.findViewById<DrawerLayout>(R.id.drawer_layout)!!
                    draverLayout.openDrawer(GravityCompat.START)
                    val categoryListView = view?.findViewById<ListView>(R.id.category_list_view)!!
                    //слушатель нажатий на элемент бокового меню
                    categoryListView.onItemClickListener =
                        object : AdapterView.OnItemClickListener {
                            override fun onItemClick(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val itemValue =
                                    categoryListView.getItemAtPosition(position) as String
                                viewModel.filterContactlist(itemValue)
                                draverLayout.closeDrawer(GravityCompat.START)
                                viewModel.openDrawerComplete()
                            }
                        }
                }
            }
        )
        //наблюдатель для навигации выбранного контакта, если не равен 0
        //после вызывается displayDetailsComplete() для того чтобы ViewModel была готова для другого навигационного события
        viewModel.navigateToSelectedContact.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    ContactsFragmentDirections.actionContactsFragmentToDetailFragment(it)

                )
                viewModel.displayDetailsComplete()
            }
        })
        // Устанавливает адаптер RecyclerView с лямбдой ContactListener, которая
        // сообщает viewModel, когда происходит нажатие на элемент списка
        val manager = LinearLayoutManager(activity)
        binding.contactList.layoutManager = manager

        binding.contactList.adapter =
            ContactLinearAdapter(ContactLinearAdapter.ContactListener {
                viewModel.displayContactDetails(it)
            })
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        //обновляем список когда возвращаемся в фрагмент
        viewModel.updateContactList()

        val item = menu.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        //добавить стрелочку рядом с крестиком
        //searchView.isSubmitButtonEnabled = true

        //поиск контакта
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                    viewModel.searhContactlist(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    viewModel.searhContactlist(newText)
                return false
            }
        })
    }

    //слушатель кнопки для открытия бокового
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> viewModel.openDrawerStart()
            else -> Log.i(ContentValues.TAG, "Не работает")
        }
        return super.onOptionsItemSelected(item)
    }

}


