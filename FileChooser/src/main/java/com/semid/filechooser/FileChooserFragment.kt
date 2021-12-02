package com.semid.filechooser

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.StrictMode
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

class FileChooserFragment(private var fragment: Fragment) {
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
                fragment.lifecycleScope.launch {
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
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_PHOTO,
                        Utils.getPath(fragment.context, result.data?.data)
                    )

                    fragment.lifecycleScope.launch {
                        _fileSharedFlow.emit(fileModel)
                    }
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

                    fragment.lifecycleScope.launch {
                        _fileSharedFlow.emit(fileModel)
                    }
                }
            }
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == RESULT_OK) {
                    val bitmap = it.data?.extras?.get("data") as Bitmap?

                    bitmap?.let {
                        val file = Utils.saveBitmap(context = fragment.context, bitmap = bitmap)

                        val fileModel = FileModel(
                            FileTypeEnum.TAKE_PHOTO,
                            file.path
                        )

                        fragment.lifecycleScope.launch {
                            _fileSharedFlow.emit(fileModel)
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
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                fragment.lifecycleScope.launch {
                    _permissionSharedFlow.emit(isGranted)
                }
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

                fragment.lifecycleScope.launch {
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