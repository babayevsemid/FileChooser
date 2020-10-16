package com.semid.filechooser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.semid.library.FileChooser;
import com.semid.library.FileListener;
import com.semid.library.FileModel;
import com.semid.library.enums.ChooseTypeEnum;

import java.util.ArrayList;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        init();
    }

    private void init() {
        FileListener listener = new FileListener() {
            @Override
            public void newFile(ArrayList<FileModel> files, FileModel fileModel) {
                Log.e("newFileRest", "Sd");
            }
        };

        FileChooser chooser = FileChooser.getInstance();
        chooser.addListener(listener);
        chooser.intent(ChooseTypeEnum.CHOOSE_VIDEO);
    }
}