package com.semid.library;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint({"StaticFieldLeak", "SimpleDateFormat"})
public class FileChooser implements LifecycleObserver {
    private static FileChooser instance;
    private AppCompatActivity activity;
    private Listener listener;

    private File fileFolder;

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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void checkNewFile() {
        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... voids) {
//                FileIntentUtil.checkNewFile();

                if (fileFolder.listFiles() == null)
                    return new ArrayList<>();

                return new ArrayList<>(Arrays.asList(fileFolder.listFiles()));
            }

            @Override
            protected void onPostExecute(List<File> list) {
                Log.e("list", list.size() + "");

                if (list.size() > 0) {
                    listener.newFile(ChooseType.CHOOSE_PHOTO, list.get(list.size() - 1), list.get(list.size() - 1).getPath(), null);
                }
            }
        }.execute();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void s() {
        Log.e("paus", "-as");
    }

    public void intent(ChooseType chooseType) {
        BlankFragment fragment = BlankFragment.newInstance(chooseType);
        fragment.show(activity.getSupportFragmentManager(),null);
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    public void deleteAllFiles() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if (filesPath.listFiles() == null)
//                    return null;
//
//                ListIterator<File> photoIterator = Arrays.asList(filesPath.listFiles()).listIterator();
//
//                while (photoIterator.hasNext()) {
//                    photoIterator.next().delete();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                refreshData();
//            }
//        }.execute();
//    }


//    public void deleteFile(String path) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                new File(path).delete();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                refreshData();
//            }
//        }.execute();
//    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static abstract class Listener {
        public abstract void newFile(ChooseType chooseType, File file, String path, Bitmap bitmap);
    }


}
