package com.semid.filechooser

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class FileChooserActivity(private var activity: AppCompatActivity) {
    private val _fileSharedFlow = MutableSharedFlow<FileModel>()
    val fileSharedFlow = _fileSharedFlow.asSharedFlow()

    private val _permissionSharedFlow = MutableSharedFlow<Boolean>()
    val permissionSharedFlow = _permissionSharedFlow.asSharedFlow()

    private val _permissionMultiSharedFlow = MutableSharedFlow<Boolean>()
    val permissionMultiSharedFlow = _permissionMultiSharedFlow.asSharedFlow()

    private var fileTypeEnum = FileTypeEnum.CHOOSE_PHOTO
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    private var manualPermissionLauncher: ActivityResultLauncher<String>? = null
    private var manualMultiPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private var choosePhotoLauncher: ActivityResultLauncher<Intent>? = null
    private var chooseVideoLauncher: ActivityResultLauncher<Intent>? = null
    private var takePhotoLauncher: ActivityResultLauncher<Intent>? = null

    private var takeVideoUri: Uri? = null

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
                activity.lifecycleScope.launch {
                    _permissionSharedFlow.emit(isGranted)
                }

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

                    activity.lifecycleScope.launch {
                        _fileSharedFlow.emit(fileModel)
                    }
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

                    activity.lifecycleScope.launch {
                        _fileSharedFlow.emit(fileModel)
                    }
                }
            }
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val bitmap = result.data?.extras?.get("data") as Bitmap?

                        bitmap?.let {
                            val file = Utils.saveBitmap(context = activity.applicationContext, bitmap = bitmap)

                            val fileModel = FileModel(
                                FileTypeEnum.TAKE_PHOTO,
                                file.path
                            )

                            activity.lifecycleScope.launchWhenStarted {
                                _fileSharedFlow.emit(fileModel)
                            }
                        }
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
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        takePhotoLauncher?.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private fun initManualPermission() {
        manualPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                activity.lifecycleScope.launch {
                    _permissionSharedFlow.emit(isGranted)
                }
            }
    }

    private fun initManualMultiPermission() {
        manualMultiPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                var isGranted=true

                permissions.entries.forEach {
                    if (!it.value)
                        isGranted=false
                }

                activity.lifecycleScope.launch {
                    _permissionMultiSharedFlow.emit(isGranted)
                }
            }
    }

    fun requestPermission(permission: String) {
        manualPermissionLauncher?.launch(permission)
    }

    fun multiRequestPermission(array: Array<String>) {
        manualMultiPermissionLauncher?.launch(array)
    }
}