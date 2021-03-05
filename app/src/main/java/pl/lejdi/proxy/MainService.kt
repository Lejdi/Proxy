package pl.lejdi.proxy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.ServerSocket
import java.util.*

class MainService : Service() {
    private var job : Job? = null
    private var inputSocket : ServerSocket? = null
    private var outputSocket : LocalSocket? = null

    companion object{
        var inputPort = 9222
        var started = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification() {
        val CHANNEL_ID = "pl.lejdi.proxy"
        val notifBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(CHANNEL_ID, "Proxy", NotificationManager.IMPORTANCE_HIGH)
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(chan)
        }

        val notification = notifBuilder.build()
        startForeground(2, notification)
    }

    override fun onCreate() {
        super.onCreate()
        createNotification()

        job = GlobalScope.launch {
            withContext(Dispatchers.IO){
                try {
                    inputSocket = ServerSocket(inputPort)
                    outputSocket = LocalSocket()
                    while(!tryConnecting()){
                        delay(1000)
                    }
                    while(true){
                        val client = inputSocket?.accept()
                        println("INPUT SOCKET CONNECTED")
                        val scanner = Scanner(client?.inputStream)
                        while (scanner.hasNextLine()) {
                            val line = scanner.nextLine()
                            println(line)
                            outputSocket?.outputStream?.write(line.toByteArray())
                        }
                    }
                }
                catch(e : Exception){
                    e.printStackTrace()
                }
            }
        }

        forwardData()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForwarding()
    }

    private fun tryConnecting() : Boolean{
        return try{
            outputSocket?.connect(LocalSocketAddress("chrome_devtools_remote"))
            println("chrome_devtools_remote CONNECTED")
            true
        } catch (e : Exception){
            e.printStackTrace()
            false
        }
    }

    private fun forwardData() {
        try{
            job?.start()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun stopForwarding(){
        inputSocket?.close()
        outputSocket?.close()
        job?.cancel()
        job = null
    }
}