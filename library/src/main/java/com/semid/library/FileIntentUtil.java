package com.semid.library;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.semid.library.enums.ChooseTypeEnum;

import static com.semid.library.FileUtils.getNewFileUri;

public class FileIntentUtil extends AppCompatActivity {
    private static ChooseTypeEnum lastActionType;

    public static void choosePhoto(final Fragment fragment) {
        Dexter.withContext(FileChooser.activity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        fragment.startActivityForResult(intent, ChooseTypeEnum.CHOOSE_PHOTO.id);

                        lastActionType = ChooseTypeEnum.CHOOSE_PHOTO;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public static void chooseVideo(final Fragment fragment) {
        Dexter.withContext(FileChooser.activity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        fragment.startActivityForResult(intent, ChooseTypeEnum.CHOOSE_VIDEO.id);

                        lastActionType = ChooseTypeEnum.CHOOSE_VIDEO;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public static void takeVideo(Fragment fragment, int second) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Uri uri = getNewFileUri(fragment.getContext(), "mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, second);
        fragment.startActivityForResult(intent, ChooseTypeEnum.TAKE_VIDEO.id);

        lastActionType = ChooseTypeEnum.TAKE_VIDEO;
    }

    public static void takePhoto(Fragment fragment) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uri = getNewFileUri(fragment.getContext(), "jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, ChooseTypeEnum.TAKE_PHOTO.id);

        lastActionType = ChooseTypeEnum.TAKE_PHOTO;
    }

    public static ChooseTypeEnum getLastActionType() {
        return lastActionType;
    }
}
