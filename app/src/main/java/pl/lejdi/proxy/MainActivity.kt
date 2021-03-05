package pl.lejdi.proxy

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import pl.lejdi.proxy.ui.theme.ProxyTheme

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProxyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = viewModel.inputPort.value.toString(),
                            onValueChange = { newValue -> viewModel.onInputPortChange(newValue.toInt()) },
                            label = {
                                Text("Input port")
                            }
                        )
                        if(viewModel.started.value){
                            Button(onClick = {
                                endListening()
                                MainService.started = false
                                viewModel.started.value = false
                            }) {
                                Text("STOP")
                            }
                        }
                        else{
                            Button(onClick = {
                                startListening()
                                MainService.started = true
                                viewModel.started.value = true
                            }) {
                                Text("START")
                            }
                        }
                    }
                }
            }
        }
    }


    private fun startListening() {
        MainService.inputPort = viewModel.inputPort.value
        startForegroundService(Intent(this, MainService::class.java))
    }

    private fun endListening() {
        stopService(Intent(this, MainService::class.java))
    }
}