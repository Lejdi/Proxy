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
import java.net.ServerSocket
import java.net.Socket
import java.util.*


class MainViewModel : ViewModel() {
    private val _started: MutableState<Boolean> =
        mutableStateOf(false)
    val started: State<Boolean> get() = _started

    private var job : Job? = null
    private var inputSocket : ServerSocket? = null
    private var outputSocket : Socket? = null

    fun forwardData() {
        _started.value = true
        job = viewModelScope.launch {
            withContext(Dispatchers.IO){
                inputSocket = ServerSocket(7890)
                outputSocket = Socket("192.168.1.4", 7891)

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
        }
        job?.start()
    }

    fun stopForwarding(){
        _started.value = false
        inputSocket?.close()
        outputSocket?.close()
        job?.cancel()
        job = null
    }
}