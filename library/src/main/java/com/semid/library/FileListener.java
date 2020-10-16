package com.semid.library;

import java.util.ArrayList;

public abstract class FileListener {
    public abstract void newFile(ArrayList<FileModel> files, FileModel fileModel);

    public void onChanged(ArrayList<FileModel> files) {
    }

    public void deletedFile(boolean isVideo, FileModel fileModel, int position) {
    }

    public void deletedAllFiles() {
    }
}
