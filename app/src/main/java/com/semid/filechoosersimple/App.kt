package com.semid.filechoosersimple

import android.app.Application
import com.semid.filechooser.FileChooserFragment

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FileChooserFragment.deleteTakeFiles(this)
    }
}