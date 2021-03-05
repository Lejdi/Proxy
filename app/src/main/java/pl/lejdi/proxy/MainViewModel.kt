package pl.lejdi.proxy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    val started: MutableState<Boolean> = mutableStateOf(MainService.started)

    private val _inputPort: MutableState<Int> =
        mutableStateOf(9222)
    val inputPort: State<Int> get() = _inputPort

    fun onInputPortChange(new : Int){
        this._inputPort.value = new
    }
}