package com.semid.library;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;

public class FileFolder {

    private FileFolder() {

    }

    public static File getBaseFolder(Context context) {
        File folder = new File(context.getExternalCacheDir(), getApplicationName(context));
        folder.mkdirs();

        return folder;
    }

    public static File getChildFolder(Context context) {
        File folder = new File(getBaseFolder(context), context.getClass().getSimpleName());
        folder.mkdirs();

        return folder;
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
