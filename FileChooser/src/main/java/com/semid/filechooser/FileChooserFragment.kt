package com.semid.filechooser

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import java.io.File


class FileChooserFragment(private var fragment: Fragment) {
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

    private var takePhotoUri: Uri? = null

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
                FileChooserFragment@ this.fileTypeEnum = fileTypeEnum
                permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            FileTypeEnum.TAKE_PHOTO -> {
                takePhoto()
            }
        }
    }

    private fun initReadPermissionAndNext() {
        permissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_PHOTO,
                        Utils.getPath(fragment.context, result.data?.data)
                    )

                    _fileLiveData.value = fileModel
                }
            }
    }

    private fun initChooseVideo() {
        chooseVideoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_VIDEO,
                        Utils.getPath(fragment.context, result.data?.data)
                    )

                    _fileLiveData.value = fileModel
                }
            }
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.TAKE_PHOTO,
                        Utils.getPath(fragment.context, takePhotoUri)
                    )

                    _fileLiveData.value = fileModel
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
        fragment.context?.let {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, Utils.generateFileName())
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")

            takePhotoUri = it.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )

            takePhotoUri = it.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri)

            takePhotoLauncher?.launch(intent)
        }
    }

    private fun initManualPermission() {
        manualPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _permissionLiveData.value = isGranted
            }
    }

    private fun initManualMultiPermission() {
        manualMultiPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
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

    companion object {
        fun deleteTakeFiles(context: Context) {
            Utils.getBaseFolder(context).listFiles()?.forEach {
                it.delete()
            }
        }

        fun deleteTakeFile(fileModel: FileModel) {
            if (fileModel.type == FileTypeEnum.TAKE_PHOTO)
                deleteFile(fileModel.path)
        }

        fun deleteFile(path: String?) {
            path?.let {
                CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                    File(path).delete()
                }
            }
        }
    }
}