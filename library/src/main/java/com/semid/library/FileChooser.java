package com.semid.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.semid.library.enums.ChooseTypeEnum;
import com.semid.library.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

@SuppressLint({"StaticFieldLeak", "SimpleDateFormat"})
public class FileChooser implements LifecycleObserver {
    private static FileChooser instance;
    public static AppCompatActivity activity;

    private File fileFolder;

    private List<FileListener> listeners = new ArrayList<>();
    private List<AppCompatActivity> activities = new ArrayList<>();

    private ArrayList<FileModel> chooseList = new ArrayList<>();
    private ArrayList<FileModel> takeList = new ArrayList<>();
    private ArrayList<FileModel> totalList = new ArrayList<>();

    public static FileChooser getInstance() {
        if (instance == null) {
            instance = new FileChooser();
        }

        if (!instance.activities.contains(activity))
            instance.activities.add(activity);

        instance.start();
        return instance;
    }

    private FileChooser() {

    }

    private void start() {
        fileFolder = FileFolder.getChildFolder(activity);

        activity.getLifecycle()
                .addObserver(this);
    }

    public static void setup(final Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                if (activity instanceof AppCompatActivity)
                    FileChooser.activity = (AppCompatActivity) activity;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                if (activity instanceof AppCompatActivity)
                    FileChooser.activity = (AppCompatActivity) activity;
            }
        });
    }

    public ArrayList<FileModel> getList() {
        return totalList;
    }

    public void intent(ChooseTypeEnum chooseType) {
        BlankFragment fragment = BlankFragment.newInstance(chooseType);
        fragment.show(activity.getSupportFragmentManager(), null);
    }

    public void intent(ChooseTypeEnum chooseType,int takeVideoLimitSecond) {
        BlankFragment fragment = BlankFragment.newInstance(chooseType,takeVideoLimitSecond);
        fragment.show(activity.getSupportFragmentManager(), null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
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
                boolean hasNewFile = takeList.size() < list.size();
                boolean listChanged = takeList.size() != list.size();
                takeList = list;

                for (FileModel model : list) {
                    if (!totalList.contains(new FileModel(model.getPath()))) {
                        totalList.add(0, model);
                    }
                }

                Log.e("listChanged", list.size() + "");
//
//                totalList = new ArrayList<>();
//                totalList.addAll(takeList);
//                totalList.addAll(chooseList);


                if (listChanged)
                    getListener().onChanged(totalList);

                if (list.size() > 0 && hasNewFile) {
                    getListener().newFile(totalList, totalList.get(0));
                }
            }
        }.execute();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void s() {

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
                totalList = new ArrayList<>();
                takeList = new ArrayList<>();
                chooseList = new ArrayList<>();

                getListener().onChanged(totalList);
                getListener().deletedAllFiles();
            }
        }.execute();
    }

    private FileListener getListener() {
        int activityIndex = activities.indexOf(activity);
        return listeners.get(activityIndex);
    }

    public void deleteFile(final FileModel fileModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... files) {
                if (fileModel.getPath().contains("/cache/"))
                    fileModel.getFile().delete();
                return null;
            }

            @Override
            protected void onPostExecute(Void file) {
                int index = totalList.indexOf(fileModel);

                if (totalList.contains(file))
                    totalList.remove(file);

                if (takeList.contains(file))
                    takeList.remove(file);

                if (chooseList.contains(file))
                    chooseList.remove(file);

                getListener().onChanged(totalList);
                getListener().deletedFile(fileModel.getFileType() == FileTypeEnum.VIDEO, fileModel, index);
            }
        }.execute();
    }

    public void addFileChoose(String path) {
        if (totalList.contains(FileUtils.fileToModel(new File(path))))
            return;

        totalList.add(0, FileUtils.fileToModel(new File(path)));
        chooseList.add(0, FileUtils.fileToModel(new File(path)));

        getListener().onChanged(totalList);
        getListener().newFile(totalList, totalList.get(0));
    }

    public void addListener(FileListener listener) {
        listeners.add(listener);
    }
}
