package com.example.playlistmaker.library.ui
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    private var _liveData = MutableLiveData<String>()
    val liveData: LiveData<String> = _liveData
}