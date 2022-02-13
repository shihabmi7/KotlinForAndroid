package com.shihab.kotlintoday.feature.flow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowViewModel : ViewModel() {

    private val _liveData = MutableLiveData<String>()
    val liveData = _liveData

    private val _stateFlow = MutableStateFlow("Kotlin Today")
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun stateFlowClicked() {
        _stateFlow.value ="State Flow"
    }

    fun sharedFlowClicked() {
        viewModelScope.launch {
            _sharedFlow.emit("Shared Flow")
        }
    }

    fun liveDataClicked() {
        _liveData.value ="Live Data"
    }

    fun triggerNormalFlow() : Flow<Int> {
        return flow {
            repeat(10) {
                emit(it+1)
                kotlinx.coroutines.delay(1000L)
            }
        }
    }
}