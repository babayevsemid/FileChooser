package com.semid.filechooser

class FileModel {
    var type: FileTypeEnum = FileTypeEnum.CHOOSE_PHOTO
    var path: String? = null

    constructor(type: FileTypeEnum, path: String?) {
        this.type = type
        this.path = path
    }


    fun isPhoto() = type == FileTypeEnum.CHOOSE_PHOTO || type == FileTypeEnum.TAKE_PHOTO
    fun isVideo() = type == FileTypeEnum.CHOOSE_VIDEO || type == FileTypeEnum.TAKE_VIDEO
}