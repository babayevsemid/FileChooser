package com.semid.filechooser

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class FileChooserFragment(private var fragment: Fragment) {
    private val _fileLiveData = MutableLiveData<FileModel>()
    val fileLiveData: LiveData<FileModel>
        get() = _fileLiveData

    private val _permissionLiveData = MutableLiveData<Boolean>()
    val permissionLiveData: LiveData<Boolean>
        get() = _permissionLiveData

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
                FileChooserFragment@ this.fileTypeEnum = fileTypeEnum
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
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_PHOTO,
                        getPath(fragment.context, result.data?.data)
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }
    }

    private fun initChooseVideo() {
        chooseVideoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val fileModel = FileModel(
                        FileTypeEnum.CHOOSE_VIDEO,
                        getPath(fragment.context, result.data?.data)
                    )

                    _fileLiveData.postValue(fileModel)
                }
            }
    }

    private fun initTakePhoto() {
        takePhotoLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) {
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
            fragment.registerForActivityResult(ActivityResultContracts.TakeVideo()) {
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
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

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

        takePhotoUri = getNewFileUri(fragment.context, FileTypeEnum.TAKE_PHOTO)

        takePhotoLauncher?.launch(takePhotoUri)
    }

    private fun takeVideo() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        takeVideoUri = getNewFileUri(fragment.context, FileTypeEnum.TAKE_VIDEO)

        takeVideoLauncher?.launch(takeVideoUri)
    }

    private fun takeVideoWithLimit(maxDurationSecond: Int) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        takeVideoUri = getNewFileUri(fragment.context, FileTypeEnum.TAKE_VIDEO)

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxDurationSecond)
        takeVideoDurationLauncher?.launch(intent)
    }

    private fun initManualPermission() {
        manualPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                _permissionLiveData.postValue(isGranted)
            }
    }

    private fun initManualMultiPermission() {
        manualMultiPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    if (!it.value) {
                        _permissionLiveData.postValue(false)
                        return@forEach
                    }
                }

                _permissionLiveData.postValue(true)
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
            GlobalScope.launch {
                getBaseFolder(context).listFiles()?.forEach {
                    it.delete()
                }
            }
        }

        fun deleteTakeFile(fileModel: FileModel) {
            if (fileModel.type == FileTypeEnum.TAKE_VIDEO ||
                fileModel.type == FileTypeEnum.TAKE_PHOTO
            ) {

                deleteFile(fileModel.path)
            }
        }

        fun deleteFile(path: String?) {
            GlobalScope.launch {
                File(path).delete()
            }
        }

    }
}