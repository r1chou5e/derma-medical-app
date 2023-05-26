package com.example.dermamedicalapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dermamedicalapplication.databinding.FragmentThumbnailImageViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThumbnailImageViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ThumbnailImageViewFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentThumbnailImageViewBinding

    private var imageUri: Uri? = null

    private var listener: ThumbnailImageListener? = null


    interface ThumbnailImageListener {
        fun onImageSelected(imageUri: Uri?)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThumbnailImageViewBinding.inflate(inflater, container, false)

        binding.addBtn.setOnClickListener {
            pickImageGallery()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ThumbnailImageListener) {
            listener = context
        }
    }


    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActitviyResultLauncher.launch(intent)
    }

    private val galleryActitviyResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data

                binding.thumbnailIv.setImageURI(imageUri)

                listener?.onImageSelected(imageUri)
            }
        }
    )



}