package com.semid.filechooser

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Utils {
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

    fun createImageFile(context: Context?): File? {
        val timeStamp = generateFileName()

        val storageDir = getBaseFolder(context)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun generateFileName() =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Calendar.getInstance().time)

    fun getBaseFolder(context: Context?): File? {
        return context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
}