package com.semid.library;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.semid.library.FileUtils.getBitmap;
import static com.semid.library.FileUtils.getNewFilePath;
import static com.semid.library.FileUtils.getNewFileUri;

public class FileIntentUtil extends AppCompatActivity{
    public static void choosePhoto(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, ChooseType.CHOOSE_PHOTO.id);
    }

    public static void chooseVideo(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        fragment.startActivityForResult(intent, ChooseType.CHOOSE_VIDEO.id);
    }

    public static void takeVideo(Fragment fragment) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Uri uri = getNewFileUri(fragment.getContext(), "mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        fragment.startActivityForResult(intent, ChooseType.CHOOSE_VIDEO.id);
    }

    public static void takePhoto(Fragment fragment) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uri = getNewFileUri(fragment.getContext(), "jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, ChooseType.TAKE_PHOTO.id);
    }
}
