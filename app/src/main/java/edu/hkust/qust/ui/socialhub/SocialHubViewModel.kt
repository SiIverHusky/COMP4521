package edu.hkust.qust.ui.socialhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SocialHubViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Social Hub Fragment"
    }
    val text: LiveData<String> = _text
}