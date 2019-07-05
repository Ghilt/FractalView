package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

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
    val persistable = fractalGson.toJson(list)
    prefs?.apply {
        edit().putString(PREFS_KEY_CORES, persistable).apply()
    }
}

fun Activity?.deleteAncestorCore(ancestorCore: AncestorCore) {
    val list = loadAncestorCores().filter { it.name != ancestorCore.name }

    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val persistable = fractalGson.toJson(list)
    prefs?.apply {
        edit().putString(PREFS_KEY_CORES, persistable).apply()
    }
}

fun Activity?.loadAncestorCores(): List<AncestorCore> {
    val prefs = this?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json: String = prefs?.getString(PREFS_KEY_CORES, "") ?: ""

    try {
        val savedCores = fractalGson.fromJson<List<AncestorCore>>(json) ?: emptyList()
        savedCores.forEach { it.recompileOnDeserialization() }
        return savedCores
    } catch (e: JsonSyntaxException) {
        Log.e("FractalSharedPreferences.loadAncestorCores():", "${e.message}")
    }

    return emptyList()
}

// Courtesy: https://stackoverflow.com/questions/33381384/how-to-use-typetoken-generics-with-gson-in-kotlin§
inline fun <reified T> Gson.fromJson(json: String): T? = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

val fractalGson: Gson
    get() {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(ConfigNode::class.java, GsonInterfaceAdapter())
        return builder.create()
    }
