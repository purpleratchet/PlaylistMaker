package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
            viewModel.setAppTheme()
        }

        binding.themeSwitcher.isChecked = viewModel.getDarkTheme()


        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.sendSupportEmail.setOnClickListener {
            viewModel.sendSupportEmail()
        }

        binding.openAgreementUrl.setOnClickListener {
            viewModel.openAgreementUrl()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}