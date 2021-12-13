package com.semid.filechooser

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import java.io.File


class FileChooserActivity(private var activity: AppCompatActivity) {
    private val _fileLiveData = SingleLiveEvent<FileModel>()
    val fileLiveData: LiveData<FileModel> get() = _fileLiveData

    private val _permissionLiveData = SingleLiveEvent<Boolean>()
    val permissionLiveData: LiveData<Boolean> get() = _permissionLiveData

    private val _permissionMultiLiveData = SingleLiveEvent<Boolean>()
    val permissionMultiLiveData: LiveData<Boolean> get() = _permissionMultiLiveData

    private var fileTypeEnum = FileTypeEnum.CHOOSE_PHOTO
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    private var manualPermissionLauncher: ActivityResultLauncher<String>? = null
    private var manualMultiPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private var choosePhotoLauncher: ActivityResultLauncher<Intent>? = null
    private var chooseVideoLauncher: ActivityResultLauncher<Intent>? = null
    private var takePhotoLauncher: ActivityResultLauncher<Intent>? = null

    private var takePhotoPath: String? = null

    init {
        initChoosePhoto()
        initChooseVideo()
        initTakePhoto()
        initReadPermissionAndNext()

        initManualPermission()
        initManualMultiPermission()
    }

    fun requestFile(fileTypeEnum: FileTypeEnum) {
        when (fileTypeEnum) {
            FileTypeEnum.CHOOSE_PHOTO, FileTypeEnum.CHOOSE_VIDEO -> {
                FileChooserActivity@ this.fileTypeEnum = fileTypeEnum
                permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            FileTypeEnum.TAKE_PHOTO -> takePhoto()
        }
    }

    private fun initReadPermissionAndNext() {
        permissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _permissionLiveData.value = isGranted


                if (isGranted) {
                    when (fileTypeEnum) {
                        FileTypeEnum.CHOOSE_PHOTO -> choosePhoto()
                        FileTypeEnum.CHOOSE_VIDEO -> chooseVideo()
                        else -> {

                        }
                    }
                }
            }
    }

    private fun initChoosePhoto() {
        choosePhotoLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_PHOTO,
                        Utils.getPath(activity.applicationContext, result.data?.data)
                    )

                    _fileLiveData.value = fileModel
                }
            }
    }

    private fun initChooseVideo() {
        chooseVideoLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_VIDEO,
                        Utils.getPath(activity.applicationContext, result.data?.data)
                    )

                    _fileLiveData.value = fileModel
                }
            }
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val fileModel = FileModel(
                            FileTypeEnum.TAKE_PHOTO,
                            takePhotoPath
                        )

                        _fileLiveData.value = fileModel
                    }
                }
            }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        choosePhotoLauncher?.launch(intent)
    }

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        chooseVideoLauncher?.launch(intent)
    }

    private fun takePhoto() {
        activity.applicationContext.let { context ->
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context.packageManager)
                    ?.also {
                        val photoFile: File? = Utils.createImageFile(context = context)

                        takePhotoPath = photoFile?.absolutePath

                        photoFile?.also { file ->
                            val photoURI = FileProvider.getUriForFile(
                                context, "com.example.android.fileprovider", file
                            )

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        }
                    }
            }

            takePhotoLauncher?.launch(intent)
        }
    }

    private fun initManualPermission() {
        manualPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _permissionLiveData.value = isGranted
            }
    }

    private fun initManualMultiPermission() {
        manualMultiPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                var isGranted = true

                permissions.entries.forEach {
                    if (!it.value)
                        isGranted = false
                }

                _permissionMultiLiveData.value = isGranted
            }
    }

    fun requestPermission(permission: String) {
        manualPermissionLauncher?.launch(permission)
    }

    fun multiRequestPermission(array: Array<String>) {
        manualMultiPermissionLauncher?.launch(array)
    }
}