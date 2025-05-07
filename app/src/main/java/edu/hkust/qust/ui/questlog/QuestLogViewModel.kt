package edu.hkust.qust.ui.questlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestLogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Quest Log Fragment"
    }
    val text: LiveData<String> = _text
}