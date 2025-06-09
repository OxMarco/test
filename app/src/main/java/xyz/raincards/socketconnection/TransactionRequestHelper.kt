package xyz.raincards.socketconnection

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import xyz.raincards.socketconnection.DecodeHelper.appendStrings
import xyz.raincards.socketconnection.POSRequestHelper.createTPDU
import xyz.raincards.socketconnection.POSRequestHelper.delimiter
import xyz.raincards.socketconnection.POSRequestHelper.endChar
import xyz.raincards.socketconnection.fields.Field6
import xyz.raincards.socketconnection.subfields.SubFieldO
import xyz.raincards.socketconnection.subfields.SubFieldP
import xyz.raincards.utils.Preferences

object TransactionRequestHelper {

    fun createTransactionRequest(
        subFieldO: SubFieldO,
        subFieldP: SubFieldP
    ): String {
        val tpdu = createTPDU()
        val headers = createTransactionHeaders()
        val fields = createTransactionFields(subFieldO, subFieldP)

        val temp = tpdu + headers + delimiter + fields + endChar
        val hilen = (temp.length / 256).toChar()
        val lowlen = (temp.length % 256).toChar()

        val transactionRequest = "$hilen$lowlen$temp"
        println("-----------transaction request: $transactionRequest")
        return transactionRequest
    }

    private fun createTransactionHeaders(): String {
        val now = Date()

        val deviceType = "9."
        val transmissionNumber = Preferences.getTransmissionID()
        val terminalID = "T1001870        "
        val employeeID = "      "
        val currentDate = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(now)
        val currentTime = SimpleDateFormat("HHmmss", Locale.getDefault()).format(now)
        val messageType = "F"
        val messageSubType = "O"
        val transactionCode = "00"
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

    private fun createTransactionFields(
        subFieldO: SubFieldO,
        subFieldP: SubFieldP
    ): String {
        val fieldB = "B1000"
        val fieldD = "D0"
        val fieldP = "P1"
        val fieldS = "S353647"
        val fieldd = "d" + "MAESTRO" // fixme
        val fielde = "e00"
        val fieldh = "h0010070201"
        val fieldq = "q;6799998900000060018=2512201012345678?"
//        val fieldG = "G2846E679"

        val subFieldE = ".E071" // 07 contactless, 1 pin entry
        val subFieldI = ".I978" // currency code, euro
        val field6 = Field6(subFieldE, subFieldI, subFieldO, subFieldP)

        val fields = appendStrings(
            listOf(
                fieldB,
                delimiter.toString(),
                fieldD,
                delimiter.toString(),
                fieldP,
                delimiter.toString(),
                fieldS,
                delimiter.toString(),
                fieldd,
                delimiter.toString(),
                fielde,
                delimiter.toString(),
                fieldh,
                delimiter.toString(),
                fieldq,
                delimiter.toString(),
//                fieldG,
                field6.toString()
            )
        )
        return fields
    }
}
