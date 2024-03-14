package com.example.playlistmaker.library.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.root.BottomNavigationListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var bottomNavigationListener: BottomNavigationListener? = null
    private lateinit var binding: FragmentNewPlaylistBinding
    private val PLviewModel: NewPlaylistViewModel by viewModel()
    private var imageIsLoaded = false
    private var uriOfImage: Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        } else {}
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkForDialogOutput()
            }
        })

        binding.editNameNewPlaylist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableNewPlaylistButton(p0.toString())
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.loadImageImageview.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.loadImageImageview.setImageURI(uri)
                    imageIsLoaded = true
                    uriOfImage = uri
                } else {
                    Log.d("PhotoPicker", "Обложка не выбрана")
                }
            }

        binding.backArrowNewPlaylist.setOnClickListener {
            checkForDialogOutput()
        }

        binding.loadImageImageview.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.newPlaylistButton.isEnabled = false
        binding.newPlaylistButton.setOnClickListener {
            val name = binding.editNameNewPlaylist.text.toString()
            val filepath =
                if (uriOfImage != null) PLviewModel.getNameForFile(binding.editNameNewPlaylist.text.toString()) else ""
            val playlist = Playlist(
                name = name,
                description = binding.editDescriptionNewPlaylist.text.toString(),
                filePath = filepath,
                listOfTracksId = "",
                amountOfTracks = 0
            )
            viewLifecycleOwner.lifecycleScope.launch {
                PLviewModel.insertPlaylistToDatabase(playlist)
            }
            uriOfImage?.let { saveImageToPrivateStorage(uri = it, nameOfFile = filepath) }
            val toastPhrase = "Плейлист" + " $name" + " создан"
            Toast.makeText(requireContext(), toastPhrase, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(true)
    }

    override fun onStop() {
        super.onStop()
        hideBottomNavigation(false)
    }

    private fun hideBottomNavigation(isHide: Boolean) {
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(!isHide)
    }

    private fun enableNewPlaylistButton(text: String?) {
        binding.newPlaylistButton.isEnabled = !text.isNullOrEmpty()
    }

    private fun checkForDialogOutput() {
        if (imageIsLoaded ||
            binding.editNameNewPlaylist.text.toString().isNotEmpty() ||
            binding.editDescriptionNewPlaylist.text.toString().isNotEmpty()
        ) {
            showDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить задание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
            }
            .setPositiveButton("Завершить") { dialog, which ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri, nameOfFile: String) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, nameOfFile)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}