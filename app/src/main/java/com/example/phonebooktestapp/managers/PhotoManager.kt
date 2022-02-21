package com.example.phonebooktestapp.managers

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoManager(application: Application, activity: FragmentActivity?, context: Context?) {

    var activity = activity
    var context = context
    var application = application
    private lateinit var currentPhotoPath: String
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    lateinit var photoURI: Uri


fun showDialog() {

    val pictureDialog = AlertDialog.Builder(context)
    pictureDialog.setTitle("Select Action")
    val pictureDialogItem = arrayOf(
        "Select photo from Gallery",
        "Capture photo from Camera"
    )
    pictureDialog.setItems(pictureDialogItem) { _, which ->

        when (which) {
            0 -> galleryCheckPermission(application.packageName)
            1 -> cameraCheckPermission()
        }
    }
    pictureDialog.show()
}

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
    private fun camera(){
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
                    this.photoURI = photoURI

                   // activity?.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
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
    //получение картинки из галереи
    private fun gallery() {
        Log.i(ContentValues.TAG, "Проверка галереии")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        activity?.startActivityForResult(intent, GALLERY_REQUEST_CODE)

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
                    activity?.startActivity(intent)
                    Log.i(ContentValues.TAG, "Проверка логов actyvity $activity")
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()


    }

    //получение картинки от системы, с фото или с галереи
   fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    Log.i(ContentValues.TAG, "Проверка логов avatarimg")
                    //обновим переменную для отображения картинки
                    //viewModel.updateImage()
                }
                GALLERY_REQUEST_CODE -> {
                    Log.i(ContentValues.TAG, "Проверка логов avatarimg2 ")
                    //добавим картинку
                   // viewModel.addImage(data?.data)
                }
            }
        }
    }

}