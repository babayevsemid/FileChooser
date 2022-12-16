package com.semid.filechooser

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.FileProvider
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
    private var choosePhotoLauncherAndroid13: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var chooseVideoLauncher: ActivityResultLauncher<Intent>? = null
    private var chooseVideoLauncherAndroid13: ActivityResultLauncher<PickVisualMediaRequest>? = null
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
                FileChooserFragment@ this.fileTypeEnum = fileTypeEnum

                if (isAndroidVersion13Higher) {
                    choose()
                }else {
                    permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
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
                    choose()
                }
            }
    }

    private fun choose() {
        when (fileTypeEnum) {
            FileTypeEnum.CHOOSE_PHOTO -> choosePhoto()
            FileTypeEnum.CHOOSE_VIDEO -> chooseVideo()
            else -> {

            }
        }
    }

    private fun initChoosePhoto() {
        if (isAndroidVersion13Higher) {
            choosePhotoLauncherAndroid13 =
                fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        val fileModel = FileModel(
                            FileTypeEnum.CHOOSE_PHOTO,
                            Utils.getPath(fragment.context, uri)
                        )

                        _fileLiveData.value = fileModel
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
        } else {
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
    }

    private fun initChooseVideo() {
        if (isAndroidVersion13Higher) {
            chooseVideoLauncherAndroid13 =
                fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        val fileModel = FileModel(
                            FileTypeEnum.CHOOSE_PHOTO,
                            Utils.getPath(fragment.context, uri)
                        )

                        _fileLiveData.value = fileModel
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
        } else {
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
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.TAKE_PHOTO,
                        takePhotoPath
                    )

                    _fileLiveData.value = fileModel
                }
            }
    }

    private fun choosePhoto() {
        if (isAndroidVersion13Higher) {
            choosePhotoLauncherAndroid13?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            choosePhotoLauncher?.launch(intent)
        }
    }

    private fun chooseVideo() {
        if (isAndroidVersion13Higher) {
            chooseVideoLauncherAndroid13?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            chooseVideoLauncher?.launch(intent)
        }
    }

    private fun takePhoto() {
        fragment.context?.let {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            fragment.context?.let { context ->
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(it.packageManager)
                        ?.also {
                            val photoFile: File? = Utils.createImageFile(context = context)

                            takePhotoPath = photoFile?.absolutePath

                            photoFile?.also { file ->
                                val photoURI = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider.fileChooser", file
                                )

                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            }
                        }
                }

                takePhotoLauncher?.launch(intent)
            }
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

   private val isAndroidVersion13Higher get() = Build.VERSION.SDK_INT >= 33

    companion object {
        fun deleteTakeFiles(context: Context) {
            Utils.getBaseFolder(context)?.listFiles()?.forEach {
                deleteFile(it.absolutePath)
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