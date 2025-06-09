package xyz.raincards.socketconnection.subfields

data class SubFieldO(
    val subFieldO_emv01: String,
    val subFieldO_9F27: String,
    val subFieldO_9F1A: String,
    val subFieldO_9A: String,
    val subFieldO_9F26: String,
    val subFieldO_82: String,
    val subFieldO_9F36: String,
    val subFieldO_9F37: String,
    val subFieldO_95: String,
    val subFieldO_9C: String,
    val subFieldO_5F2A: String,
    val subFieldO_9F02: String,
    val subFieldO_9F10: String
) {

    companion object {

//        @JvmStatic
//        fun createSubFieldO(iemv: Emv): SubFieldO {
//            val subFieldO_emv01 = "01"
//
//            val subFieldO_9F27 = "80"
//            //            String subFieldO_9F27 = decodeTLV(iemv.getTlvList("9F27"));
//
//            val subFieldO_9F1A = "499"
//            //            String subFieldO_9F1A = decodeTLV(iemv.getTlvList("9F1A"));
//
//            val subFieldO_9A = DecodeHelper.decodeTLV(iemv.getTlvList("9A"))
//            val subFieldO_9F26 = DecodeHelper.decodeTLV(iemv.getTlvList("9F26"))
//            val subFieldO_82 = DecodeHelper.decodeTLV(iemv.getTlvList("82"))
//            val subFieldO_9F36 = "00" + DecodeHelper.decodeTLV(iemv.getTlvList("9F36"))
//            val subFieldO_9F37 = DecodeHelper.decodeTLV(iemv.getTlvList("9F37"))
//            val subFieldO_95 = DecodeHelper.decodeTLV(iemv.getTlvList("95"))
//            val subFieldO_9C = DecodeHelper.decodeTLV(iemv.getTlvList("9C"))
//
//            val subFieldO_5F2A = "978"
//            //            String subFieldO_5F2A = decodeTLV(iemv.getTlvList("5F2A"));
//
//            val subFieldO_9F02 = DecodeHelper.decodeTLV(iemv.getTlvList("9F02"))
//
//
//            //            String subFieldO_9F10 = "000210A0000F240000000000000000000000FF";
//            val subFieldO_9F10 = DecodeHelper.decodeTLV(iemv.getTlvList("9F10"))
//
//            val subFieldO = SubFieldO(
//                subFieldO_emv01,
//                subFieldO_9F27,
//                subFieldO_9F1A,
//                subFieldO_9A,
//                subFieldO_9F26,
//                subFieldO_82,
//                subFieldO_9F36,
//                subFieldO_9F37,
//                subFieldO_95,
//                subFieldO_9C,
//                subFieldO_5F2A,
//                subFieldO_9F02,
//                subFieldO_9F10
//            )
//
//            return subFieldO
//        }
    }

    override fun toString(): String {
        val temp = ".O" +
            subFieldO_emv01 +
            subFieldO_9F27 +
            subFieldO_9F1A +
            subFieldO_9A +
            subFieldO_9F26 +
            subFieldO_82 +
            subFieldO_9F36 +
            subFieldO_9F37 +
            subFieldO_95 +
            subFieldO_9C +
            subFieldO_5F2A +
            subFieldO_9F02 +
            subFieldO_9F10

        return temp
    }
}
