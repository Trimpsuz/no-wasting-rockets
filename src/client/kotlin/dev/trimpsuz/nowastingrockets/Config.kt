package dev.trimpsuz.nowastingrockets

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object Config {
    const val MOD_ID: String = "no-wasting-rockets"
    const val MOD_NAME: String = "No Wasting Rockets"
    val MOD_VERSION: String = Utils.Companion.getModVersion(MOD_ID)
    const val CONFIG_FILE_NAME: String = "$MOD_ID.json"

    var isEnabled: Boolean = true
    var forceBoostElytra: Boolean = false

    private val configFile = File(Utils.Companion.getConfigDirectory(), CONFIG_FILE_NAME)
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun load() {
        if (configFile.exists() && configFile.isFile && configFile.canRead()) {
            configFile.reader().use {
                val configMap: Map<String, Boolean> = gson.fromJson(it, Map::class.java) as Map<String, Boolean>
                isEnabled = configMap["isEnabled"] ?: isEnabled
                forceBoostElytra = configMap["forceBoostElytra"] ?: forceBoostElytra
            }
        }
    }

    fun save() {
        configFile.writer().use {
            gson.toJson(mapOf("isEnabled" to isEnabled, "forceBoostElytra" to forceBoostElytra), it)
        }
    }
}
