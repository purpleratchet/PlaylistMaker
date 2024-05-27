package com.example.playlistmaker.library.ui.fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.root.BottomNavigationListener
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
abstract class BaseFragment : Fragment() {
    var bottomNavigationListener: BottomNavigationListener? = null
    lateinit var binding: FragmentNewPlaylistBinding
    val viewModel: NewPlaylistViewModel by viewModel()
    var imageIsLoaded: Boolean = false
    var uriOfImage: Uri? = null

    var backArrowImageView: ImageView? = null
    var loadImageImageView: ImageView? = null
    var editNameEditText: TextInputEditText? = null
    var editDescriptionEditText: TextInputEditText? = null
    var newPlayListButton: Button? = null

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            enableNewPlaylistButton(p0.toString())
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    val pickMediaCommon =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            loadImageImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
            loadImageImageView?.setImageURI(uri)
            imageIsLoaded = true
            uriOfImage = uri
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        } else {
            throw IllegalArgumentException("Activity must implement BottomNavigationListener")
        }
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
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(true)
    }
    override fun onStop() {
        super.onStop()
        hideBottomNavigation(false)
    }
    fun initViews() {
        backArrowImageView = binding.backArrowNewPlaylist
        loadImageImageView = binding.loadImageImageview
        editNameEditText = binding.editNameNewPlaylist
        editDescriptionEditText = binding.editDescriptionNewPlaylist
        newPlayListButton = binding.newPlaylistButton
    }
    fun hideBottomNavigation(isHide: Boolean) {
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(!isHide)
    }

    fun enableNewPlaylistButton(text: String?) {
        newPlayListButton?.isEnabled  = !text.isNullOrEmpty()
    }

    fun saveImageToPrivateStorage(uri: Uri, nameOfFile: String) {
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