package xyz.raincards.socketconnection.subfields

data class SubFieldP(
    val subFieldP_emv01: String,
    val subFieldP_5F34: String,
    val subFieldP_9F35: String,
    val subFieldP_9F34: String,
    val subFieldP_9F09: String,
    val subFieldP_84: String
) {

    companion object {

//        @JvmStatic
//        fun createSubFieldP(iemv: Emv): SubFieldP {
//
//            val subFieldP_emv01 = "01"
//            val subFieldP_5F34 = DecodeHelper.decodeTLV(iemv.getTlvList("5F34"))
//            val subFieldP_9F35 = DecodeHelper.decodeTLV(iemv.getTlvList("9F35"))
//            val subFieldP_9F34 = DecodeHelper.decodeTLV(iemv.getTlvList("9F34"))
//            val subFieldP_9F09 = DecodeHelper.decodeTLV(iemv.getTlvList("9F09"))
//            val subFieldP_84 = DecodeHelper.decodeTLV(iemv.getTlvList("84"))
//
//            val subFieldP = SubFieldP(
//                subFieldP_emv01,
//                subFieldP_5F34,
//                subFieldP_9F35,
//                subFieldP_9F34,
//                subFieldP_9F09,
//                subFieldP_84
//            )
//            return subFieldP
//        }
    }

    override fun toString(): String {
        val temp = ".P" +
            subFieldP_emv01 +
            subFieldP_5F34 +
            subFieldP_9F35 +
            subFieldP_9F34 +
            subFieldP_9F09 +
            subFieldP_84
        return temp
    }
}
