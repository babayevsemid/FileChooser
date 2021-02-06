package com.semid.filechooser

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File


class FileChooserActivity(private var activity: AppCompatActivity) {
    private val _fileLiveData = SingleLiveEvent<FileModel>()
    val fileLiveData: LiveData<FileModel>
        get() = _fileLiveData

    private val _permissionLiveData = SingleLiveEvent<Boolean>()
    val permissionLiveData: LiveData<Boolean>
        get() = _permissionLiveData

    private val _manualPermissionLiveData = SingleLiveEvent<Boolean>()
    val manualPermissionLiveData: LiveData<Boolean>
        get() = _manualPermissionLiveData

    private val _manualMultiPermissionLiveData = SingleLiveEvent<Boolean>()
    val manualMultiPermissionLiveData: LiveData<Boolean>
        get() = _manualMultiPermissionLiveData

    private var fileTypeEnum = FileTypeEnum.CHOOSE_PHOTO
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    private var manualPermissionLauncher: ActivityResultLauncher<String>? = null
    private var manualMultiPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private var choosePhotoLauncher: ActivityResultLauncher<Intent>? = null
    private var chooseVideoLauncher: ActivityResultLauncher<Intent>? = null
    private var takePhotoLauncher: ActivityResultLauncher<Uri>? = null
    private var takeVideoLauncher: ActivityResultLauncher<Uri>? = null
    private var takeVideoDurationLauncher: ActivityResultLauncher<Intent>? = null

    private var takePhotoUri: Uri? = null
    private var takeVideoUri: Uri? = null

    init {
        initChoosePhoto()
        initChooseVideo()
        initTakePhoto()
        initTakeVideo()
        initTakeVideoDuration()
        initReadPermissionAndNext()

        initManualPermission()
        initManualMultiPermission()
    }

    fun requestFile(fileTypeEnum: FileTypeEnum, maxDurationSecond: Int = 0) {
        when (fileTypeEnum) {
            FileTypeEnum.CHOOSE_PHOTO, FileTypeEnum.CHOOSE_VIDEO -> {
                FileChooserActivity@ this.fileTypeEnum = fileTypeEnum
                permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            FileTypeEnum.TAKE_PHOTO -> takePhoto()
            FileTypeEnum.TAKE_VIDEO -> {
                if (maxDurationSecond == 0)
                    takeVideo()
                else
                    takeVideoWithLimit(maxDurationSecond)
            }
        }
    }

    private fun initReadPermissionAndNext() {
        permissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _permissionLiveData.postValue(isGranted)

                if (isGranted) {
                    when (fileTypeEnum) {
                        FileTypeEnum.CHOOSE_PHOTO -> choosePhoto()
                        FileTypeEnum.CHOOSE_VIDEO -> chooseVideo()
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
                        getPath(activity.applicationContext, result.data?.data)
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }
    }

    private fun initChooseVideo() {
        chooseVideoLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_VIDEO,
                        getPath(activity.applicationContext, result.data?.data)
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }

    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            activity.registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) {
                    val fileModel = FileModel(
                        FileTypeEnum.TAKE_PHOTO,
                        takePhotoUri?.path
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }
    }

    private fun initTakeVideo() {
        takeVideoLauncher =
            activity.registerForActivityResult(ActivityResultContracts.TakeVideo()) {
                if (hasFile(takeVideoUri?.path)) {
                    val fileModel = FileModel(
                        FileTypeEnum.TAKE_VIDEO,
                        takeVideoUri?.path
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }
    }

    private fun initTakeVideoDuration() {
        takeVideoDurationLauncher =
            activity.registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val fileModel = FileModel(
                        FileTypeEnum.TAKE_VIDEO,
                        takeVideoUri?.path
                    )

                    _fileLiveData.postValue(fileModel)
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

        takePhotoUri = getNewFileUri(activity, FileTypeEnum.TAKE_PHOTO)

        takePhotoLauncher?.launch(takePhotoUri)
    }

    private fun takeVideo() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        takeVideoUri = getNewFileUri(activity, FileTypeEnum.TAKE_VIDEO)

        takeVideoLauncher?.launch(takeVideoUri)
    }

    private fun takeVideoWithLimit(maxDurationSecond: Int) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        takeVideoUri = getNewFileUri(activity, FileTypeEnum.TAKE_VIDEO)

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takeVideoUri)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxDurationSecond)
        takeVideoDurationLauncher?.launch(intent)
    }

    private fun initManualPermission() {
        manualPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _manualPermissionLiveData.postValue(isGranted)
            }
    }

    private fun initManualMultiPermission() {
        manualMultiPermissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    if (!it.value) {
                        _manualMultiPermissionLiveData.postValue(false)
                        return@forEach
                    }
                }

                _manualMultiPermissionLiveData.postValue(true)
            }
    }

    fun requestPermission(permission: String) {
        manualPermissionLauncher?.launch(permission)
    }

    fun multiRequestPermission(array: Array<String>) {
        manualMultiPermissionLauncher?.launch(array)
    }
}