package com.example.phonebooktestapp.detailui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.Toast
import com.karumi.dexter.Dexter
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.phonebooktestapp.R
import com.example.phonebooktestapp.database.Contact
import com.example.phonebooktestapp.databinding.DetailFragmentViewBinding
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }
    private lateinit var currentPhotoPath: String
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //разрешение на установку меню в фрагменте
        setHasOptionsMenu(true)

        val binding: DetailFragmentViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.detail_fragment_view,
            container,
            false
        )

        @Suppress("UNUSED_VARIABLE")
        val application = requireNotNull(activity).application

        //переменная для хранения имени пакета(нужен для формирования intent, переход в настройки разрешения приложения)
        val packageName = application.packageName

        binding.lifecycleOwner = this
        val contact = DetailFragmentArgs.fromBundle(requireArguments()).selectedContact
        val viewModelFactory = DetailViewModelFactory(contact, application)
        Log.i(ContentValues.TAG, "DetailFragment=" + contact?.name)
        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(DetailViewModel::class.java)

        // переход назад
        viewModel.closeDetilFragment.observe(this, {
            if (it != null) {
                val navController = Navigation.findNavController(activity!!, R.id.myNavHostFragment)
                navController.navigateUp()
            }
        })

        //загрузка изображения
        binding.imageEdit.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )
            pictureDialog.setItems(pictureDialogItem) { _, which ->

                when (which) {
                    0 -> galleryCheckPermission(packageName)
                    1 -> cameraCheckPermission()
                }
            }
            pictureDialog.show()

        }

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

                var contactNew = Contact()
                // если контакт не новый копируем выбранный контакт и если есть изменения записываем
                if (!viewModel.newContact)
                    contactNew = viewModel.selectedContact.value!!.copy()

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

        return binding.root
    }

    //создание файла для картинки(после фото)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // создадим имя для картинки
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //получение картинки с камеры
    private fun camera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Создание файла, куда будет помещено фото
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // продолжим только если файл создан
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //установка разрешений Open_Document
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    viewModel.addImage(photoURI)

                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    //получение картинки из галереи
    private fun gallery() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //получение картинки от системы, с фото или с галереи
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {

                    //обновим переменную для отображения картинки
                    viewModel.updateImage()
                }
                GALLERY_REQUEST_CODE -> {
                    //добавим картинку
                    viewModel.addImage(data?.data)
                }
            }
        }
    }

    //проверка разрешения на использование галереи
    private fun galleryCheckPermission(packageName: String) {

        Dexter.withContext(context).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                Toast.makeText(
                    context,
                    "Вы не дали разрешения на добавление изображения",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission(packageName)
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }
        }).onSameThread().check()
    }

    //проверка разрешения на использование камеры
    private fun cameraCheckPermission() {

        Dexter.withContext(context)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                }
            ).onSameThread().check()
    }

    //показ диалога запроса разрешений
    fun showRotationalDialogForPermission(packageName: String) {

        AlertDialog.Builder(context)
            .setMessage(
                "У вас нет разрешения, "
                        + "чтобы работала эта функция нужно дать разрешение на доступ к диску!!!"
            )
            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
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


