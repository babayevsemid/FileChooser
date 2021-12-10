package com.semid.filechooser

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun hasFile(path: String?): Boolean {
        return path?.let {
            File(it).exists()
        } ?: false
    }

    fun getPath(context: Context?, uri: Uri?): String? {
        print(uri)
        if (uri == null)
            return null

        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()

            val path = cursor.getString(columnIndex)
            cursor.close()

            path
        } else null
    }

    fun getNewFileUri(context: Context?, fileTypeEnum: FileTypeEnum): Uri? {
        val endWith = "jpg"

        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Calendar.getInstance().time)

        val file = File(getBaseFolder(context).toString() + "/" + timeStamp + "." + endWith)
        return Uri.fromFile(file)
    }

    fun getBaseFolder(context: Context?): File {
        val folder = File(context?.externalCacheDir, getApplicationName(context))
        folder.mkdirs()
        return folder
    }

    private fun getApplicationName(context: Context?): String {
        if (context == null)
            return ""

        val applicationInfo = context.applicationInfo
        return applicationInfo.packageName
    }

    fun saveBitmap(context: Context?, bitmap: Bitmap): File {
        val newFile = File(getNewFileUri(context, FileTypeEnum.CHOOSE_PHOTO)?.path.toString())
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.writeTo(FileOutputStream(newFile))
        stream.close()
        return newFile
    }
}