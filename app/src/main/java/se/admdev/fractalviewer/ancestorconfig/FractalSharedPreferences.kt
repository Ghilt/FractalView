package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import se.admdev.fractalviewer.ancestorconfig.model.*
import java.lang.reflect.Type

private const val prefsName = "se.admdev.fractalviewer.canvas"
private const val prefsKey = "canvas_TODO_MANY_SAVES"

fun Activity?.saveConfigurationNodes(core: AncestorCore) {
    val prefs = this?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val persistable = Gson().toJson(core)
    prefs?.apply{
        edit().putString(prefsKey, persistable).apply()
    }
}

fun Activity?.loadConfigurationNodes(): List<AncestorCore> {
    val gson = Gson()
    val prefs = this?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val json = prefs?.getString(prefsKey, "")
    val parser = JsonParser()
    val parsedCore = parser.parse(json).asJsonObject
    val array = parsedCore.getAsJsonArray(AncestorCore.JSON_LIST_NAME)
    val list: List<ConfigNode> = array.toList().map { gson.fromJson(it, determineType(it)) as ConfigNode}
    return listOf(AncestorCore(list))
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
