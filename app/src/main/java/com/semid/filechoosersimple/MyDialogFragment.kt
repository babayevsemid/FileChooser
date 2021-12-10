package com.semid.filechoosersimple

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.semid.filechooser.FileChooserFragment
import com.semid.filechooser.FileTypeEnum
import com.semid.filechoosersimple.databinding.FragmentMyDialogBinding


class MyDialogFragment : DialogFragment(), View.OnClickListener {
    private val fileChooser = FileChooserFragment(this)

    private val binding by lazy {
        FragmentMyDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileChooser.fileLiveData
            .observe(viewLifecycleOwner) {
                Log.e("saa,", it.path.toString())

                Glide.with(requireContext())
                    .load(it.path)
                    .into(binding.coverImg)
            }

        binding.choosePhotoTxt.setOnClickListener(this)
        binding.chooseVideoTxt.setOnClickListener(this)
        binding.takePhotoTxt.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.choosePhotoTxt -> fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO)
            binding.chooseVideoTxt -> fileChooser.requestFile(FileTypeEnum.CHOOSE_VIDEO)
            binding.takePhotoTxt -> fileChooser.requestFile(FileTypeEnum.TAKE_PHOTO)
        }
    }
}