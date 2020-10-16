package com.semid.library;

import android.graphics.Bitmap;

import com.semid.library.enums.FileTypeEnum;

import java.io.File;

public class FileModel {
    private FileTypeEnum fileType;
    private File file;
    private String path;

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypeEnum fileType) {
        this.fileType = fileType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getBitmap() {
        return null;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
