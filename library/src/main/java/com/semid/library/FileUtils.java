package com.semid.library;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

import com.semid.library.enums.ChooseTypeEnum;
import com.semid.library.enums.FileTypeEnum;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileUtils {
    public static Uri getNewFileUri(Context context, String endWith) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File file = new File(FileFolder.getChildFolder(context) + "/" + timeStamp + "." + endWith);
        Log.e("getNewFileUri",file+"");
        return Uri.fromFile(file);
    }

    public static File getNewFilePath(Context context, String endWith) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File file = new File(FileFolder.getChildFolder(context) + "/" + timeStamp + "." + endWith);

        return file;
    }

    public static Bitmap getBitmap(Context context, Uri contentUri, int sizeKb) {
        if (contentUri == null)
            return null;

        Bitmap myBitmap;
        if (sizeKb > 0)
            myBitmap = getResizedBitmap(BitmapFactory.decodeFile(getFilePath(context, contentUri)), sizeKb);
        else
            myBitmap = BitmapFactory.decodeFile(getFilePath(context, contentUri));

        if (myBitmap == null)
            return null;

        try {
            String filePath = getFilePath(context, contentUri);

            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
        }

        return myBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        if (image == null)
            return null;

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getFilePath(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String result = cursor.getString(column_index);
            cursor.close();

            return result;
        } else
            return "";
    }

    public static ChooseTypeEnum getFileType(File file) {
        String path = file.getAbsolutePath();
        return null;
    }

    public static ArrayList<FileModel> filesToModel(@Nullable List<File> files) {
        ArrayList<FileModel> list = new ArrayList<>();

        if (files == null)
            return list;

        for (File file : files) {
            list.add(fileToModel(file));
        }

        return list;
    }

    public static ArrayList<FileModel> filesToModel(@Nullable File[] files) {
        ArrayList<FileModel> list = new ArrayList<>();

        if (files == null)
            return list;

        for (File file : files) {
            list.add(fileToModel(file));
        }

        return list;
    }

    public static FileModel fileToModel(File file) {
        FileModel model = new FileModel();
        model.setFile(file);
        model.setPath(file.getPath());
        model.setFileType(FileTypeEnum.byFile(file));

        return model;
    }
}
