package com.example.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistViewModel : ViewModel() {

    private var _liveData = MutableLiveData<String>()
    val liveData: LiveData<String> = _liveData

}