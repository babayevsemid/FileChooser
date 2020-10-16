package com.semid.filechooser;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.semid.filechooser.databinding.ActivityMainBinding;
import com.semid.library.FileChooser;
import com.semid.library.FileModel;
import com.semid.library.enums.ChooseTypeEnum;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initAdapter();
        init();
    }

    private void initAdapter() {
        adapter = new FileAdapter(getApplicationContext(), new FileAdapter.Listener() {
            @Override
            public void onDelete(File file) {

            }
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void init() {
        FileChooser.Listener listener = new FileChooser.Listener() {
            @Override
            public void newFile(ArrayList<FileModel> files, FileModel fileModel) {
                adapter.updateList(files);
            }
        };

        FileChooser chooser = FileChooser.getInstance(this);
        chooser.setListener(listener);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choosePhoto:
                FileChooser.getInstance(this)
                        .intent(ChooseTypeEnum.CHOOSE_PHOTO);
                break;
            case R.id.chooseVideo:
                FileChooser.getInstance(this)
                        .intent(ChooseTypeEnum.CHOOSE_VIDEO);
                break;
            case R.id.takePhoto:
                FileChooser.getInstance(this)
                        .intent(ChooseTypeEnum.TAKE_PHOTO);
                break;
            case R.id.takeVideo:
                FileChooser.getInstance(this)
                        .intent(ChooseTypeEnum.TAKE_VIDEO);
                break;
        }
    }
}