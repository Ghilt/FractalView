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

private const val prefsName = "se.admdev.fractalviewer.canvas"
private const val prefsKey = "fractalAncestorCores.v1.0.0"

fun Activity?.saveAncestorCore(ancestorCore: AncestorCore, name: String) {
    val core = AncestorCore(ancestorCore.configNodes, name)
    val list = mutableListOf(core)
    list.addAll(loadAncestorCores())

    val prefs = this?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val persistable = Gson().toJson(list)
    prefs?.apply{
        edit().putString(prefsKey, persistable).apply()
    }
}

fun Activity?.loadAncestorCores(): List<AncestorCore> {
    val gson = Gson()
    val prefs = this?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val json = prefs?.getString(prefsKey, "")
    val parser = JsonParser()
    val parsedCoreList = parser.parse(json)

    return if (parsedCoreList.isJsonArray) {
        parsedCoreList.asJsonArray.toList().map { c ->
            val jsonCore = c.asJsonObject
            val array = jsonCore.getAsJsonArray(AncestorCore.JSON_LIST_NAME)
            val list: List<ConfigNode> = array.toList().map { gson.fromJson(it, determineType(it)) as ConfigNode}
            AncestorCore(list, jsonCore.get(JSON_NAME_NAME)?.toString())
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
