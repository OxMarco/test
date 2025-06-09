package xyz.raincards.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.models.enums.TransactionCode
import xyz.raincards.socketconnection.DecodeHelper.decodeServerResponse
import xyz.raincards.socketconnection.HandshakeRequestHelper.createHandShakeRequest
import xyz.raincards.socketconnection.LogonRequestHelper
import xyz.raincards.socketconnection.ParsedResponse
import xyz.raincards.socketconnection.TransactionRequestHelper.createTransactionRequest
import xyz.raincards.socketconnection.subfields.SubFieldO
import xyz.raincards.socketconnection.subfields.SubFieldP
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEventsViewModel
import xyz.raincards.utils.Preferences

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseEventsViewModel() {

    private lateinit var writer: BufferedWriter

    init {
//        startClient()
    }

    private fun startClient() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val socket = Socket("195.66.185.22", 2500)
            println("------------🔗 Client connected to server")

            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            sendLogonRequest()

            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val buffer = CharArray(1024)
            var charsRead: Int

            while (reader.read(buffer).also { charsRead = it } != -1) {
                val text = String(buffer, 0, charsRead)
                println("------------📥 Client received: $text")
                val decodedResponse = decodeServerResponse(text)
                parseResponse(decodedResponse)
            }

            socket.close()
            println("------------🛑 Client closed connection")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendLogonRequest() = CoroutineScope(Dispatchers.IO).launch {
        val logonRequest = LogonRequestHelper.createLogonRequest()
        println("------------📥 Client sent: $logonRequest")
        writer.write(logonRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendHandShakeRequest() = CoroutineScope(Dispatchers.IO).launch {
        val handShakeRequest = createHandShakeRequest()
        println("------------📥 Client sent: $handShakeRequest")
        writer.write(handShakeRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendTransactionRequest(subFieldO: SubFieldO, subFieldP: SubFieldP) =
        CoroutineScope(Dispatchers.IO).launch {
            val transactionRequest = createTransactionRequest(subFieldO, subFieldP)
            println("------------📥 Client sent: $transactionRequest")
            writer.write(transactionRequest)
            writer.flush()
            Preferences.incrementTransmissionID()
        }

    private fun parseResponse(response: ParsedResponse) {
        when (response.getType()) {
            TransactionCode.ERROR -> {
                println("------------ error")
            }

            TransactionCode.LOGON -> {
                if (response.isSuccessful()) {
                    sendHandShakeRequest()
                } else {
                    println("------------Logon error")
                }
            }

            TransactionCode.HANDSHAKE -> {
                if (response.isSuccessful()) {
                    println("------------ handshake success")
                } else {
                    println("------------ handshake error")
                }
            }

            TransactionCode.TRANSACTION -> {
                if (response.isSuccessful()) {
                    println("------------ transaction success")
//                    binding.enterAmount.setText("")
//                    binding.enterAmount.clearFocus()
//                    Toast.makeText(
//                        thisme.demo.view.MainActivity,
//                        "Transaction completed successfully",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    println("------------ transaction success")
                }
            }
        }
    }

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
//        data object ChargeSuccess : Event()
//        data object ChargeError : Event()
    }
}
