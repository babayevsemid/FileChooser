package com.semid.library;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.semid.library.enums.ChooseTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

@SuppressLint({"StaticFieldLeak", "SimpleDateFormat"})
public class FileChooser implements LifecycleObserver {
    private static FileChooser instance;
    private AppCompatActivity activity;
    private Listener listener;

    private File fileFolder;
    private ArrayList<FileModel> list = new ArrayList<>();

    private FileChooser() {

    }

    public static FileChooser getInstance(AppCompatActivity activity) {
        if (instance == null)
            instance = new FileChooser();

        instance.setActivity(activity);
        return instance;
    }

    private void setActivity(AppCompatActivity activity) {
        this.activity = activity;

        activity.getLifecycle().addObserver(this);

        fileFolder = FileFolder.getBaseFolder(activity.getApplicationContext());
    }

    public void intent(ChooseTypeEnum chooseType) {
        BlankFragment fragment = BlankFragment.newInstance(chooseType);
        fragment.show(activity.getSupportFragmentManager(), null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void checkNewFile() {
        new AsyncTask<Void, Void, ArrayList<FileModel>>() {
            @Override
            protected ArrayList<FileModel> doInBackground(Void... voids) {
                if (fileFolder.listFiles() == null)
                    return new ArrayList<>();

                return FileUtils.filesToModel(fileFolder.listFiles());
            }

            @Override
            protected void onPostExecute(ArrayList<FileModel> list) {
                Log.e("size",list.size()+"");
                Log.e("FileChooser",FileChooser.this.list.size()+"");

                boolean hasNewFile = FileChooser.this.list.size() < list.size();
                FileChooser.this.list = list;

                if (list.size() > 0 && hasNewFile) {
                    listener.onChanged(list);
                    listener.newFile(list, list.get(0));
                }
            }
        }.execute();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void s() {
        Log.e("paus", "-as");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        deleteAllFiles();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        deleteAllFiles();
    }

    public void deleteAllFiles() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (fileFolder.listFiles() == null)
                    return null;

                ListIterator<File> photoIterator = Arrays.asList(fileFolder.listFiles()).listIterator();

                while (photoIterator.hasNext()) {
                    photoIterator.next().delete();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                list = new ArrayList<>();
                listener.onChanged(list);
                listener.deletedAllFiles();
            }
        }.execute();
    }

    public void deleteFile(final File file) {
        new AsyncTask<File, Void, File>() {
            @Override
            protected File doInBackground(File... files) {
                files[0].delete();
                return files[0];
            }

            @Override
            protected void onPostExecute(File file) {
                list.remove(file);
                listener.onChanged(list);
                listener.deletedFile(file.getAbsolutePath().endsWith("mp4"), FileUtils.fileToModel(file));
            }
        }.execute(file);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static abstract class Listener {
        public abstract void newFile(ArrayList<FileModel> files, FileModel fileModel);

        public void onChanged(ArrayList<FileModel> files) {
        }

        public void deletedFile(boolean isVideo, FileModel fileModel) {
        }

        public void deletedAllFiles() {
        }
    }
}
