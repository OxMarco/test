package xyz.raincards.socketconnection.fields

import xyz.raincards.socketconnection.subfields.SubFieldO
import xyz.raincards.socketconnection.subfields.SubFieldP

data class Field6(
    val subFieldE: String,
    val subFieldI: String,
    val subFieldO: SubFieldO,
    val subFieldP: SubFieldP
) {
    override fun toString(): String {
        val temp = "6$subFieldE$subFieldI$subFieldO$subFieldP"
        return temp
    }
}