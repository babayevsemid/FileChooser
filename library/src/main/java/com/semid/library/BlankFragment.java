package com.semid.library;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.semid.library.enums.ChooseTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;
import static com.semid.library.FileUtils.getBitmap;
import static com.semid.library.FileUtils.getNewFilePath;

public class BlankFragment extends DialogFragment {
    private ChooseTypeEnum type = ChooseTypeEnum.CHOOSE_PHOTO;

    public static BlankFragment newInstance(ChooseTypeEnum type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.id);

        BlankFragment fragment = new BlankFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null)
            type = ChooseTypeEnum.byId(getArguments().getInt("type"));

        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        switch (type) {
            case CHOOSE_PHOTO:
                FileIntentUtil.choosePhoto(this);
                break;
            case CHOOSE_VIDEO:
                FileIntentUtil.chooseVideo(this);
                break;
            case TAKE_PHOTO:
                FileIntentUtil.takePhoto(this);
                break;
            case TAKE_VIDEO:
                FileIntentUtil.takeVideo(this);
                break;
            case CHOOSE_FILE:
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, R.style.BaseDialogTheme);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (type) {
                case CHOOSE_PHOTO:
                    choosePhotoResult(data);
                    break;
                case CHOOSE_VIDEO:
                    chooseVideoResult(data);
                    break;
            }
        }

        dismiss();
    }

    private void choosePhotoResult(Intent data) {
        FileChooser.getInstance()
                .addFileChoose(getPath(data.getData()));
    }

    private void chooseVideoResult(Intent data) {
        FileChooser.getInstance()
                .addFileChoose(getPath(data.getData()));
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}