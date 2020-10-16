package com.semid.library;

import androidx.annotation.Nullable;

import com.semid.library.enums.FileTypeEnum;

import java.io.File;

public class FileModel {
    private FileTypeEnum fileType;
    private File file;
    private String path;

    public FileModel() {
    }

    public FileModel(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof FileModel) {
            if (((FileModel) obj).getPath().equals(getPath()))
                return true;
        }
        return super.equals(obj);
    }

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

    public void setPath(String path) {
        this.path = path;
    }
}
