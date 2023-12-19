package com.example.playlistmaker.sharing.domain.api

interface SharingRepository {
    fun shareApp()
    fun sendSupportEmail()
    fun openAgreementUrl()
}