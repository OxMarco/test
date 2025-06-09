package xyz.raincards.socketconnection

import io.github.binaryfoo.RootDecoder
import xyz.raincards.socketconnection.POSRequestHelper.controlCharsWithTPDULength
import xyz.raincards.socketconnection.POSRequestHelper.delimiter
import xyz.raincards.socketconnection.POSRequestHelper.headerResponseLength

object DecodeHelper {

    fun decodeServerResponse(text: String): ParsedResponse {
        val hilen = text[0].code
        val lowlen = text[1].code
        val messageLength = hilen * 256 + lowlen

        // -2, hilen,lowlen at the start
//        if (text.length - 2 == messageLength) {
        val header = text.substring(
            controlCharsWithTPDULength,
            controlCharsWithTPDULength + headerResponseLength
        )
        val transactionCode = header.substring(40, 42)
        val responseCode = header.takeLast(3)

        val fields = text.substringAfter(header)
        var gValue = ""
        if (fields.isNotEmpty() && fields.contains(delimiter)) {
            val parts = fields.split(delimiter)
            parts.find { it.startsWith("g") }?.let {
                gValue = it.drop(1)
            }
        }

        return ParsedResponse(transactionCode, responseCode, gValue)
//        } else {
//            return ParsedResponse("-1")
//        }
    }

    fun decodeTLV(data: String): String {
        return RootDecoder().decode(data, "EMV", "constructed")[0].getDecodedData()
    }

    fun convertHexBytesToAsciiString(hex: String): Char {
        // Step 2: Convert hex string to integer (base 16)

        val decimalValue = hex.toInt(16)

        // Step 3: Format it as a hexadecimal value with 0x prefix
        val hexFormatted = String.format("0x%02X", decimalValue)

        // Step 4: Convert decimal to character
        val character = decimalValue.toChar()

        // Step 5: Print each step
//        System.out.println("Input Hex String: " + hex);
//        System.out.println("Decimal Value: " + decimalValue);
//        System.out.println("Hex with Prefix: " + hexFormatted);
//        System.out.println("Corresponding Character: '" + character + "'");
//        System.out.println("-----------------------");
        return character
    }

    fun appendStrings(string: List<String?>): String {
        val sb = StringBuilder()
        for (txt in string) {
            sb.append(txt)
        }
        return sb.toString()
    }
}
