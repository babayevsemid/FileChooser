package com.semid.library.enums;

import java.io.File;

public enum FileTypeEnum {
    PHOTO, VIDEO, OTHER;

    public static FileTypeEnum byFile(File file) {
        return byPath(file.getAbsolutePath());
    }

    public static FileTypeEnum byPath(String path) {
        boolean isPhoto = path.toLowerCase().endsWith("jpg") || path.toLowerCase().endsWith("png");
        boolean isVideo = path.endsWith("mp4");

        if (isPhoto) {
            return PHOTO;
        } else if (isVideo) {
            return VIDEO;
        } else
            return OTHER;
    }
}
