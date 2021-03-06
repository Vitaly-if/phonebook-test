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
import com.example.phonebooktestapp.storage.StorageManager
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

class PhotoManager() {

    private lateinit var mContext: Context
    lateinit var callBackUri: (Uri) -> Unit
    private lateinit var currentPhotoPath: String
    val CAMERA_REQUEST_CODE = 1
    val GALLERY_REQUEST_CODE = 2
    var photoURI: Uri? = null
    private lateinit var mApplication: Application
    private lateinit var mActivity: FragmentActivity

    constructor(context: Context) : this() {

        mContext = context

    }

    companion object {

        @Volatile
        private var INSTANCE: PhotoManager? = null




        fun getInstance(
            context: Context?
        ): PhotoManager? =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: context?.let {
                    buildStorage(it).also {
                        INSTANCE = it
                    }
                }

            }
        private fun buildStorage(context: Context) =
            PhotoManager(context)
    }


        fun getPhotoUri(
            modePhotoManager: ModePhotoManager,
            application: Application,
            activity: FragmentActivity,
            clickListener: (Uri) -> Unit
        ) {
            mApplication = application
            mActivity = activity
            callBackUri = clickListener

            when (modePhotoManager) {

                ModePhotoManager.SHOW_DIALOG -> showDialog()

                ModePhotoManager.GET_FROM_CAMERA -> cameraCheckPermission()

                ModePhotoManager.GET_FROM_GALLERY -> galleryCheckPermission(mApplication.packageName)
            }

        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    CAMERA_REQUEST_CODE -> {
                    }
                    GALLERY_REQUEST_CODE -> {
                        Log.i(ContentValues.TAG, "???????????????? ?????????? gallery $data")
                        photoURI = data?.data
                    }
                }
                photoURI?.let { callBackUri.invoke(it) }
            }

        }

        private fun buildStorage(
            context: Context,
        ) = PhotoManager(context)

        fun showDialog() {

            val pictureDialog = AlertDialog.Builder(mContext)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )
            pictureDialog.setItems(pictureDialogItem) { _, which ->

                when (which) {
                    0 -> galleryCheckPermission(mApplication.packageName)
                    1 -> cameraCheckPermission()
                }
            }
            pictureDialog.show()
        }

        //???????????????? ???????????????????? ???? ?????????????????????????? ??????????????
        private fun galleryCheckPermission(packageName: String) {

            Dexter.withContext(mContext).withPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    gallery()

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    Toast.makeText(
                        mContext,
                        "???? ???? ???????? ???????????????????? ???? ???????????????????? ??????????????????????",
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

        //???????????????? ???????????????????? ???? ?????????????????????????? ????????????
        private fun cameraCheckPermission() {

            Dexter.withContext(mContext)
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

        @Throws(IOException::class)
        private fun createImageFile(): File {
            // ???????????????? ?????? ?????? ????????????????
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }


        }

        //?????????????????? ???????????????? ?? ????????????
        fun camera() {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                takePictureIntent.resolveActivity(mActivity.packageManager)?.also {
                    // ???????????????? ??????????, ???????? ?????????? ???????????????? ????????
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // ?????????????????? ???????????? ???????? ???????? ????????????
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            mContext,
                            "com.example.android.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        //?????????????????? ???????????????????? Open_Document
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        this.photoURI = photoURI

                        mActivity.startActivityForResult(
                            takePictureIntent,
                            CAMERA_REQUEST_CODE
                        )
                    }
                }
            }
        }

        //?????????????????? ???????????????? ???? ??????????????
        fun gallery() {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
                it.addCategory(Intent.CATEGORY_OPENABLE)
                it.type = "image/*"
                it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            mActivity.startActivityForResult(intent, GALLERY_REQUEST_CODE)

        }

        //?????????? ?????????????? ?????????????? ????????????????????
        fun showRotationalDialogForPermission(packageName: String) {

            AlertDialog.Builder(mContext)
                .setMessage(
                    "?? ?????? ?????? ????????????????????, "
                            + "?????????? ???????????????? ?????? ?????????????? ?????????? ???????? ???????????????????? ???? ???????????? ?? ??????????!!!"
                )
                .setPositiveButton("Go TO SETTINGS") { _, _ ->

                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        mActivity.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }

                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }.show()


        }



}