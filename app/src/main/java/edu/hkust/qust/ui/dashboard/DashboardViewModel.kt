package edu.hkust.qust.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private var _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    var text: LiveData<String> = _text

    private var _name = MutableLiveData<String>().apply {
        value = "User"
    }
    var name: LiveData<String> = _name

    private var _level = MutableLiveData<String>().apply {
        value = "5"
    }
    var level: LiveData<String> = _level

    private var _strength = MutableLiveData<String>().apply {
        value = "20"
    }
    var strength: LiveData<String> = _strength

    private var _IQ = MutableLiveData<String>().apply {
        value = "15"
    }
    var IQ: LiveData<String> = _IQ

    private var _HP = MutableLiveData<String>().apply {
        value = "100"
    }
    var HP: LiveData<String> = _HP

    private var _remainingTask = MutableLiveData<String>().apply {
        value = "5"
    }
    var remainingTask: LiveData<String> = _remainingTask


    // Update methods
    fun updateText(newText: String) {
        _text.value = newText
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateLevel(newLevel: String) {
        _level.value = newLevel
    }

    fun updateRemainingTask(newTask: String) {
        _remainingTask.value = newTask
    }
}