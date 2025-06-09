package xyz.raincards.socketconnection

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import xyz.raincards.socketconnection.DecodeHelper.appendStrings
import xyz.raincards.socketconnection.POSRequestHelper.endChar
import xyz.raincards.utils.Preferences

object HandshakeRequestHelper {

    fun createHandShakeRequest(): String {
        val tpdu = POSRequestHelper.createTPDU()
        val headers = createHandshakeHeaders()

        val temp = tpdu + headers + endChar
        val hilen = (temp.length / 256).toChar()
        val lowlen = (temp.length % 256).toChar()

        val handShakeRequest = "$hilen$lowlen$temp"
        println("-----------handShake request: $handShakeRequest")
        return handShakeRequest
    }

    private fun createHandshakeHeaders(): String {
        val now = Date()

        val deviceType = "9."
        val transmissionNumber = Preferences.Companion.getTransmissionID()
        val terminalID = "T1001870        "
        val employeeID = "      "
        val currentDate = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(now)
        val currentTime = SimpleDateFormat("HHmmss", Locale.getDefault()).format(now)
        val messageType = "A"
        val messageSubType = "O"
        val transactionCode = "95"
        val processingFlag1 = "1"
        val processingFlag2 = "5"
        val processingFlag3 = "0"
        val responseCode = "000"

        val headers = appendStrings(
            listOf(
                deviceType,
                transmissionNumber,
                terminalID,
                employeeID,
                currentDate,
                currentTime,
                messageType,
                messageSubType,
                transactionCode,
                processingFlag1,
                processingFlag2,
                processingFlag3,
                responseCode
            )
        )
        //        System.out.println("-----------headers: " + headers);
        return headers
    }
}
