package ru.nextf.measurements

/**
 * Created by addd on 16.02.2018.
 */

import android.preference.PreferenceManager
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.IOException

class MyWebSocket : WebSocketListener() {
    private lateinit var socket: WebSocket
    private var socketCallback: SocketCallback? = null
    override fun onOpen(myWebSocket: WebSocket, response: Response) {
        socket = myWebSocket
        println("OPEN: ")
        val sp = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val token = sp.getString(APP_TOKEN, "")
        myWebSocket.send("{\"event\":\"auth\", \"data\" : { \"token\" : \"$token\"}}")
    }

    fun run() {
        val client = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()

        val request = Request.Builder()
                .url("http://natcom-crm.nextf.ru/ws/connect")
                .build()
        val websocket = client.newWebSocket(request, this)
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown()
    }

    override fun onMessage(myWebSocket: WebSocket, text: String) {
        socketCallback?.message(text)

        println("MESSAGE: " + text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//        println("MESSAGE: " + bytes.hex())
    }

    override fun onClosing(myWebSocket: WebSocket, code: Int, reason: String) {
        myWebSocket.close(1000, null)
        println("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        t?.printStackTrace()
        try {
            Thread.sleep(1000)
        } catch (e: IOException) {
            run()
        }
        run()
    }

    interface SocketCallback {
        fun message(text: String)
    }

    fun registerSocketCallback(callback: SocketCallback?) {
        this.socketCallback = callback
    }

    fun close() {
        socket.close(1000, "Goodbye, World!")
    }
}
