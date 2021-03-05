package pl.lejdi.proxy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.*


class MainViewModel : ViewModel() {
    private val _started: MutableState<Boolean> =
        mutableStateOf(false)
    val started: State<Boolean> get() = _started

    private val _inputPort: MutableState<Int> =
        mutableStateOf(10000)
    val inputPort: State<Int> get() = _inputPort

    fun onInputPortChange(new : Int){
        this._inputPort.value = new
    }

    private val _outputPort: MutableState<Int> =
        mutableStateOf(10001)
    val outputPort: State<Int> get() = _outputPort

    fun onOutputPortChange(new : Int){
        this._outputPort.value = new
    }

    private val _outputIp: MutableState<String> =
        mutableStateOf("127.0.0.1")
    val outputIp: State<String> get() = _outputIp

    fun onOutputIpChange(new : String){
        this._outputIp.value = new
    }

    private var job : Job? = null
    private var inputSocket : ServerSocket? = null
    private var outputSocket : Socket? = null

    fun forwardData() {
        try{

            _started.value = true
            job = viewModelScope.launch {
                withContext(Dispatchers.IO){
                    try {
                        inputSocket = ServerSocket(_inputPort.value)
                        outputSocket = Socket(_outputIp.value, _outputPort.value)

                        while(true){
                            val client = inputSocket?.accept()
                            val scanner = Scanner(client?.inputStream)
                            while (scanner.hasNextLine()) {
                                val line = scanner.nextLine()
                                println(line)
                                outputSocket?.outputStream?.write(line.toByteArray())
                            }
                        }
                    }
                    catch(e : Exception){
                        _started.value = false
                        e.printStackTrace()
                    }

                }
            }
            job?.start()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun stopForwarding(){
        _started.value = false
        inputSocket?.close()
        outputSocket?.close()
        job?.cancel()
        job = null
    }
}