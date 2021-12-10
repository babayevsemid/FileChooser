package com.semid.filechoosersimple

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.semid.filechooser.FileChooserActivity
import com.semid.filechooser.FileTypeEnum
import com.semid.filechoosersimple.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val fileChooser = FileChooserActivity(this)

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fileChooser.fileLiveData
            .observe(this) {
                Log.e("file", it.path + "///")
                Log.e("file", File(it.path).length().toString())

                Glide.with(applicationContext)
                    .load(it.path)
                    .into(binding.coverImg)
            }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.choosePhotoTxt -> fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO)
            binding.chooseVideoTxt -> fileChooser.requestFile(FileTypeEnum.CHOOSE_VIDEO)
            binding.takePhotoTxt -> fileChooser.requestFile(FileTypeEnum.TAKE_PHOTO)
            binding.showFragment -> showFragment()
        }
    }

    private fun showFragment() {
        MyDialogFragment().show(
            supportFragmentManager,
            MyDialogFragment::class.simpleName
        )
    }
}