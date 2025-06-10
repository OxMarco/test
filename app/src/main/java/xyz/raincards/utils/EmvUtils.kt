package xyz.raincards.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.nexgo.oaf.apiv3.emv.AidEntity
import com.nexgo.oaf.apiv3.emv.AidEntryModeEnum
import com.nexgo.oaf.apiv3.emv.CapkEntity
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import org.w3c.dom.Element

/**
 * cn.nexgo.inbas.components.emv
 * Author: zhaojiangon 2018/1/31 17:31.
 * Modified by Brad2018/4/23 : add test file
 */
class EmvUtils(val context: Context) {

    val capkList: MutableList<CapkEntity?>?
        get() {
            val capkEntityList: MutableList<CapkEntity?> = ArrayList<CapkEntity?>()
            val gson = Gson()
            val parser = JsonParser()

            val jsonArray = parser.parse(readAssetsTxt("emv_capk.json")).getAsJsonArray()

            if (jsonArray == null) {
                return null
            }

            for (user in jsonArray) {
                val userBean = gson.fromJson<CapkEntity?>(user, CapkEntity::class.java)
                capkEntityList.add(userBean)
            }
            return capkEntityList
        }

    val aidList: MutableList<AidEntity?>?
        get() {
            val aidEntityList: MutableList<AidEntity?> = ArrayList<AidEntity?>()
            val gson = Gson()
            val parser = JsonParser()
            val jsonArray: JsonArray?

            jsonArray =
                parser.parse(readAssetsTxt("emv_aid.json")).getAsJsonArray()

            if (jsonArray == null) {
                return null
            }

            for (user in jsonArray) {
                val userBean = gson.fromJson<AidEntity>(user, AidEntity::class.java)
                val jsonObject = user.getAsJsonObject()
                if (jsonObject != null) {
                    if (jsonObject.get("emvEntryMode") != null) {
                        val emvEntryMode = jsonObject.get("emvEntryMode").getAsInt()
                        userBean.setAidEntryModeEnum(AidEntryModeEnum.entries[emvEntryMode])
                        Log.d("nexgo", "emvEntryMode" + userBean.getAidEntryModeEnum())
                    }
                }

                aidEntityList.add(userBean)
            }
            return aidEntityList
        }

    private fun readAssetsTxt(fileName: String): String? {
        try {
            val `is`: InputStream = context.getAssets().open(fileName)

            val size = `is`.available()
            // Read the entire asset into a local byte buffer.
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            // Convert the buffer into a string.
            return String(buffer, charset("utf-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun readFile(filePath: String?): String? {
        try {
            val `is`: InputStream = FileInputStream(filePath)
            val size = `is`.available()
            // Read the entire asset into a local byte buffer.
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            // Convert the buffer into a string.
            return String(buffer, charset("utf-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getElementAttr(element: Element?, attr: String?): String? {
        if (element == null) return ""
        if (element.hasAttribute(attr)) {
            return element.getAttribute(attr)
        }
        return ""
    }
}