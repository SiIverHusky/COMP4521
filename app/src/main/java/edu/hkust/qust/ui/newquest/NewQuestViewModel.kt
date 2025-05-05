package edu.hkust.qust.ui.newquest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewQuestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is New Quest Fragment"
    }
    val text: LiveData<String> = _text
}