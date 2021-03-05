package pl.lejdi.proxy

import android.net.LocalSocket
import android.net.LocalSocketAddress
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
        mutableStateOf(9222)
    val inputPort: State<Int> get() = _inputPort

    fun onInputPortChange(new : Int){
        this._inputPort.value = new
    }

    private var job : Job? = null
    private var inputSocket : ServerSocket? = null
    private var outputSocket : LocalSocket? = null

    fun forwardData() {
        try{

            _started.value = true
            job = viewModelScope.launch {
                withContext(Dispatchers.IO){
                    try {
                        inputSocket = ServerSocket(_inputPort.value)
                        outputSocket = LocalSocket()
                        outputSocket?.connect(LocalSocketAddress("chrome_devtools_remote"))

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