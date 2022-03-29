package com.example.phonebooktestapp.ui.details

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.phonebooktestapp.R
import com.bumptech.glide.Glide
import com.example.phonebooktestapp.databinding.FragmentDetailsBinding
import com.example.phonebooktestapp.managers.ModePhotoManager
import com.example.phonebooktestapp.managers.PhotoManager
import com.example.phonebooktestapp.models.ContactModel
import com.example.phonebooktestapp.models.ContactsGroupModel
import kotlinx.coroutines.delay


class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var application: Application
    var listGroupNew = mutableListOf<ContactsGroupModel>()


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupBind(inflater, container)
        setupUI()
        bindVM()

        binding.constraintView.setOnClickListener {
            hideKeyboardFrom(context, view)
            Log.i(ContentValues.TAG, "проверка нажатия")
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

        addGroup()
    }

    private fun addGroup() {
        binding.floatingAddGroup.setOnClickListener {
            binding.editGroupLinear.visibility = VISIBLE
        }

        binding.buttonSaveGroup.setOnClickListener {
            if (TextUtils.isEmpty(binding.editCategory.text)) {
                binding.editGroupLinear.visibility = View.GONE
            } else {
                val category = binding.editCategory.text.toString()
                val categoryNew = ContactsGroupModel(0, category)
                viewModel.insertGroup(categoryNew)
                binding.editCategory.text.clear()
                binding.editGroupLinear.visibility = View.GONE
            }
        }
    }

    private fun loadRadioGroup() {

        viewModel.listContactsGroup.observe(this) { group ->

            group.let { groupList ->
                listGroupNew = groupList as MutableList<ContactsGroupModel>
                val list = listGroupNew.map { it.name }
                binding.radioButtonGroup.removeAllViews()
                list.forEachIndexed { index, element ->
                    val newRadioButton = RadioButton(context)
                    newRadioButton.text = element
                    newRadioButton.id = index
                    binding.radioButtonGroup.addView(newRadioButton)
                }

                checkRadioButton()
            }
        }
    }

    private fun checkRadioButton() {
        if (viewModel.selectContactGroupRB <= listGroupNew.size - 1) {
            val radioButton = binding.radioButtonGroup.getChildAt(viewModel.selectContactGroupRB)
            binding.radioButtonGroup.check(radioButton.id)
            Log.i(ContentValues.TAG, "Проверка установки указателя")
        }
    }

    private fun loadImage() {
        //загрузка изображения
        binding.imageEdit.setOnClickListener {

            val photoManager = PhotoManager.getInstance(context)

            context?.let { context ->
                activity?.let { activity ->
                    photoManager?.getPhotoUri(ModePhotoManager.SHOW_DIALOG, application, activity) {

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

        // наблюдатель за нажатием кнопки save
        viewModel.saveContact.observe(this, {
            if (it) {
                val checkedRadioButtonId = binding.radioButtonGroup.checkedRadioButtonId
                val selectedRadioButton =
                    view?.findViewById<RadioButton>(checkedRadioButtonId)

                var contactNew = ContactModel(0L, "", "", "", 0L)
                // если контакт не новый копируем выбранный контакт и если есть изменения записываем
                if (!viewModel.newContact) {
                    val ContactOld = viewModel.selectedContactsTable.value!!
                    contactNew.contactId = ContactOld.contactId
                    contactNew.contactAvatarImg = ContactOld.contactAvatarImg
                    contactNew.phone = ContactOld.phone
                    contactNew.name = ContactOld.name
                    contactNew.groupID = ContactOld.groupID

                }

                binding.apply {
                    contactNew.contactAvatarImg = viewModel?.avatarImgString?.value ?: ""
                    contactNew.name = editTextName.text.toString()
                    contactNew.phone = editTextPhone.text.toString()
                    contactNew.groupID = selectedRadioButton?.id?.toLong()!!
                    Log.i(ContentValues.TAG, "проверка ID ${contactNew.groupID}")

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


