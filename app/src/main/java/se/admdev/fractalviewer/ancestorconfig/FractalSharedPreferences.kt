package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import se.admdev.fractalviewer.ancestorconfig.model.*
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore.Companion.JSON_NAME_NAME
import java.lang.reflect.Type

private const val PREFS_NAME = "se.admdev.fractalviewer.canvas"
private const val PREFS_KEY_CORES = "fractalAncestorCores.v1.0.0"
private const val PREFS_KEY_FIRST_TIME_USER = "fractalFirstTimeUser.v1.0.0"

fun Activity?.isFirstTimeUser(): Boolean {
    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs?.getBoolean(PREFS_KEY_FIRST_TIME_USER, true) ?: true
}

fun Activity?.saveIsFirstTimeUser(firstTimeUser: Boolean) {
    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs?.apply {
        edit().putBoolean(PREFS_KEY_FIRST_TIME_USER, firstTimeUser).apply()
    }
}

fun Activity?.saveAncestorCore(ancestorCore: AncestorCore, name: String) {
    val core = AncestorCore(ancestorCore.configNodes, name)
    val list = mutableListOf(core)
    list.addAll(loadAncestorCores())

    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val persistable = Gson().toJson(list)
    prefs?.apply {
        edit().putString(PREFS_KEY_CORES, persistable).apply()
    }
}

fun Activity?.deleteAncestorCore(ancestorCore: AncestorCore) {
    val list = loadAncestorCores().filter { it.name != ancestorCore.name }

    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val persistable = Gson().toJson(list)
    prefs?.apply {
        edit().putString(PREFS_KEY_CORES, persistable).apply()
    }
}

fun Activity?.loadAncestorCores(): List<AncestorCore> {
    val gson = Gson()
    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs?.getString(PREFS_KEY_CORES, "")
    val parser = JsonParser()
    val parsedCoreList = parser.parse(json)

    return if (parsedCoreList.isJsonArray) {
        parsedCoreList.asJsonArray.toList().map { c ->
            val jsonCore = c.asJsonObject
            val array = jsonCore.getAsJsonArray(AncestorCore.JSON_LIST_NAME)
            val list: List<ConfigNode> = array.toList().map { gson.fromJson(it, determineType(it)) as ConfigNode }
            val name = jsonCore.get(JSON_NAME_NAME)?.toString()?.trim { it == '"' } // TODO Wrestle with Gson nicely
            AncestorCore(list, name)
        }
    } else {
        emptyList()
    }
}

/**
 * Gson do not play nice with sub classes
 * Seems a bit like bs to have to do it like this; I mean they landed on the moon, and they make me do this?
 */
private fun determineType(elem: JsonElement): Type {
    return when {
        elem.asJsonObject.has(GroupOperationConfigNode.ID_FIELD) -> GroupOperationConfigNode::class.java
        elem.asJsonObject.has(ConditionalConfigNode.ID_FIELD) -> ConditionalConfigNode::class.java
        elem.asJsonObject.has(OperationConfigNode.ID_FIELD) -> OperationConfigNode::class.java
        else -> throw JsonParseException("FractalSharedPreferences: Could not recognize as config node: $elem")
    }
}
