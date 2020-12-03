package com.semid.filechooser

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun getPath(context: Context?, uri: Uri?): String? {
    print(uri)
    if (uri == null)
        return null

    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, null)
    return if (cursor != null) {
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    } else null
}

fun getSelectedImagePath(context: Context, uri: Uri?): String? {
    var cursor: Cursor? = context.contentResolver.query(uri!!, null, null, null, null)
    cursor?.moveToFirst()

    var documentId = cursor?.getString(0)
    documentId = documentId?.substring(documentId.lastIndexOf(":") + 1)
    cursor?.close()
    cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        null, MediaStore.Images.Media._ID + " = ? ", arrayOf(documentId), null
    )
    cursor?.moveToFirst()
    val path = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
    cursor?.close()
    return path
}

fun getNewFileUri(context: Context?, fileTypeEnum: FileTypeEnum): Uri? {
    var endWith = "jpg"

    when (fileTypeEnum) {
        FileTypeEnum.CHOOSE_VIDEO, FileTypeEnum.TAKE_VIDEO -> endWith = "mp4"
    }

    val timeStamp =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

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
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
    else context.getString(stringId)
}