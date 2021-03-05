package pl.lejdi.proxy

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
                        Button(onClick = {
                            if (viewModel.started.value) {
                                endListening()
                            } else {
                                startListening()
                            }

                        }) {
                            if(viewModel.started.value){
                                Text("STOP")
                            }
                            else{
                                Text("START")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startListening() {
        viewModel.forwardData()
    }

    private fun endListening() {
        viewModel.stopForwarding()
    }
}