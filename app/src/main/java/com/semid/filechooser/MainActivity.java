package com.semid.filechooser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.semid.library.ChooseType;
import com.semid.library.FileChooser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileChooser.Listener listener=new FileChooser.Listener() {
            @Override
            public void newFile(ChooseType chooseType, File file, String path, Bitmap bitmap) {
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
            }
        };

        FileChooser chooser = FileChooser.getInstance(this);
        chooser.setListener(listener);
        chooser.intent(ChooseType.CHOOSE_VIDEO);
    }
}