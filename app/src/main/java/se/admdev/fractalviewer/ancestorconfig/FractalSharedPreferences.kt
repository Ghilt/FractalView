package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore

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
    val prefs = this?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val core: AncestorCore = Gson().fromJson(prefs?.getString(prefsKey, ""), AncestorCore::class.java)
    return listOf(core)
}