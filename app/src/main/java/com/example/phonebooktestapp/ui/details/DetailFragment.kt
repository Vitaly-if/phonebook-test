package com.example.phonebooktestapp.ui.details

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.phonebooktestapp.R
import com.example.phonebooktestapp.storage.ContactsTable
import com.bumptech.glide.Glide
import com.example.phonebooktestapp.databinding.FragmentDetailsBinding
import com.example.phonebooktestapp.managers.PhotoManager


class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var application: Application

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupBind(inflater, container)
        setupUI()
        bindVM()

        binding.constraintView.setOnClickListener{
            hideKeyboardFrom(context, view)
            Log.i(ContentValues.TAG,"проверка нажатия")
        }
        return binding.root
    }
    private fun hideKeyboardFrom(context: Context?, view: View?) {
        val imm =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    private fun setupBind(inflater: LayoutInflater, container: ViewGroup?) {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        @Suppress("UNUSED_VARIABLE")
        application = requireNotNull(activity).application

        val contact = DetailFragmentArgs.fromBundle(requireArguments()).selectedContact
        val viewModelFactory = DetailViewModelFactory(contact, application)
        Log.i(ContentValues.TAG, "DetailFragment=" + contact?.name)
        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(DetailViewModel::class.java)
        binding.lifecycleOwner = this
    }

    private fun setupUI() {
        loadImage()

        setHasOptionsMenu(true)

        loadRadioGroup()
    }

    private fun loadRadioGroup() {

        val dataCategory= resources.getStringArray(R.array.category)
        val radioGroup = binding.radioButtonGroup

        dataCategory.forEachIndexed { index, element ->

            val newRadioButton = RadioButton(context)
            newRadioButton.text = element
            newRadioButton.setId(index)
            radioGroup.addView(newRadioButton)
        }
    }

    private fun loadImage() {
        //загрузка изображения
        binding.imageEdit.setOnClickListener {

            PhotoManager.getInstance(context)

            context?.let { context ->
                activity?.let { activity ->
                    PhotoManager.getPhotoUri(context, application, activity) {

                        viewModel.addImage(it)
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun bindVM() {
        // наблюдатель за изменением картинки
        viewModel.avatarImgString.observe(this, {
            val tempuri = Uri.parse(it)

            Glide.with(this)
                .load(tempuri) // Uri of the picture
                .circleCrop()
                .into(binding.imageView)
        })
        // первоначальное заполнение radioButton

        val radioButton = binding.radioButtonGroup.getChildAt(viewModel.selectContactGroupRB)
        binding.radioButtonGroup.check(radioButton.id)


        // наблюдатель за нажатием кнопки save
        viewModel.saveContact.observe(this, {
            if (it) {

                var contactNew = ContactsTable()
                // если контакт не новый копируем выбранный контакт и если есть изменения записываем
                if (!viewModel.newContact)
                    contactNew = viewModel.selectedContactsTable.value!!.copy()

                val checkedRadioButtonId = binding.radioButtonGroup.checkedRadioButtonId
                val selectedRadioButton =
                    view?.findViewById<RadioButton>(checkedRadioButtonId)

                binding.apply {
                    contactNew.contactAvatarImg = viewModel?.avatarImgString?.value ?: ""
                    contactNew.name = editTextName.text.toString()
                    contactNew.phone = editTextPhone.text.toString()
                    contactNew.category = selectedRadioButton?.text.toString()

                }
                if (viewModel.newContact)
                    viewModel.insertContact(contactNew)
                else viewModel.updateContact(contactNew)
                viewModel.saveContactComplete()
            }
        })
        // переход назад
        viewModel.closeDetilFragment.observe(this, {
            if (it != null) {
                val navController = Navigation.findNavController(activity!!, R.id.myNavHostFragment)
                navController.navigateUp()
            }
        })
    }

    //добавление меню SAVE
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //обработка нажатия меню SAVE
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.SAVE -> {
                viewModel.saveContactStart(); return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}


