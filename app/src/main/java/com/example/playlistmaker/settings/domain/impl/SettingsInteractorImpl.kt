package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
    private val sharingRepository: SharingRepository
) :
    SettingsInteractor {
    override fun getDarkTheme(): Boolean {
        return settingsRepository.getDarkTheme()
    }

    override fun setDarkTheme(enabled: Boolean) {
        settingsRepository.setDarkTheme(enabled)
    }

    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun sendSupportEmail() {
        sharingRepository.sendSupportEmail()
    }

    override fun openAgreementUrl() {
        sharingRepository.openAgreementUrl()
    }

    override fun setAppTheme() {
        settingsRepository.setAppTheme()
    }
}