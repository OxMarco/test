package xyz.raincards.socketconnection

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import xyz.raincards.socketconnection.POSRequestHelper.createTPDU
import xyz.raincards.socketconnection.POSRequestHelper.delimiter
import xyz.raincards.socketconnection.POSRequestHelper.endChar
import xyz.raincards.utils.Preferences

object LogonRequestHelper {

    fun createLogonRequest(): String {
        val tpdu = createTPDU()
        val headers = createLogonHeaders()
        val fieldH = "h0010030111"

        val temp = tpdu + headers + delimiter + fieldH + endChar
        val hilen = (temp.length / 256).toChar()
        val lowlen = (temp.length % 256).toChar()

        val logonRequest = "$hilen$lowlen$temp"
        println("-----------logonRequest: $logonRequest")
        return logonRequest
    }

    private fun createLogonHeaders(): String {
        val now = Date()

        val deviceType = "9."
        val transmissionNumber = Preferences.Companion.getTransmissionID()
        val terminalID = "T1001870        "
        val employeeID = "      "
        val currentDate = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(now)
        val currentTime = SimpleDateFormat("HHmmss", Locale.getDefault()).format(now)
        val messageType = "A"
        val messageSubType = "O"
        val transactionCode = "50"
        val processingFlag1 = "1"
        val processingFlag2 = "5"
        val processingFlag3 = "0"
        val responseCode = "000"

        val headers = DecodeHelper.appendStrings(
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
