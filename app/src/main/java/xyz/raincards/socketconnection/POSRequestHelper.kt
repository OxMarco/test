package xyz.raincards.socketconnection

import xyz.raincards.socketconnection.DecodeHelper.convertHexBytesToAsciiString

object POSRequestHelper {

    val delimiter = convertHexBytesToAsciiString("1C")
    val endChar = convertHexBytesToAsciiString("03")
    const val tpduLength = 5
    const val controlCharsWithTPDULength = 2 + tpduLength
    const val headerResponseLength = 48

    fun createTPDU(): String {
        val hexList: List<String> = mutableListOf("60", "00", "14", "00", "00")

        val sb = StringBuilder()
        for (hex in hexList) {
            sb.append(convertHexBytesToAsciiString(hex))
        }

        val tpdu = sb.toString()
        //        System.out.println("-----------tpdu: " + tpdu);
        return tpdu
    }
}
