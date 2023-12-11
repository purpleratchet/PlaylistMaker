package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareApp()
    fun sendSupportEmail()
    fun openAgreementUrl()
}