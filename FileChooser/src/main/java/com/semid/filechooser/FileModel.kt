package com.semid.filechooser

class FileModel(var type: FileTypeEnum, var path: String?) {
    override fun equals(other: Any?): Boolean {
        if (other is FileModel)
            return path == other.path

        return super.equals(other)
    }
}