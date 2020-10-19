package com.semid.filechooser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.semid.filechooser.databinding.ActivityMainBinding;
import com.semid.library.FileChooser;
import com.semid.library.FileListener;
import com.semid.library.FileModel;
import com.semid.library.enums.ChooseTypeEnum;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.takePhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Test.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return false;
            }
        });
        initAdapter();
        init();
    }

    private void initAdapter() {
        adapter = new FileAdapter(getApplicationContext(), new FileAdapter.Listener() {
            @Override
            public void onDelete(FileModel model) {
                FileChooser.getInstance()
                        .deleteFile(model);
            }
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void init() {
        FileListener listener = new FileListener() {
            @Override
            public void newFile(ArrayList<FileModel> files, FileModel fileModel) {
                adapter.updateList(files);
            }

            @Override
            public void onChanged(ArrayList<FileModel> files) {

            }

            @Override
            public void deletedFile(boolean isVideo, FileModel fileModel, int position) {
                adapter.removeItem(position);
            }

            @Override
            public void deletedAllFiles() {

            }
        };

        FileChooser chooser = FileChooser.getInstance();
        chooser.addListener(listener);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choosePhoto:
                FileChooser.getInstance()
                        .intent(ChooseTypeEnum.CHOOSE_PHOTO);
                break;
            case R.id.chooseVideo:
                FileChooser.getInstance()
                        .intent(ChooseTypeEnum.CHOOSE_VIDEO,100);
                break;
            case R.id.takePhoto:
                FileChooser.getInstance()
                        .intent(ChooseTypeEnum.TAKE_PHOTO);
                break;
            case R.id.takeVideo:
                FileChooser.getInstance()
                        .intent(ChooseTypeEnum.TAKE_VIDEO);
                break;
        }
    }
}