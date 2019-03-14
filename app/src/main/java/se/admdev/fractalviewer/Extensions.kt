package se.admdev.fractalviewer

import android.view.View
import android.widget.Button

val Boolean?.viewVisibility
    get() = if (this == true) View.VISIBLE else View.GONE

fun Button.setTextIfNotNull(newText: String?){
    newText?.let { text = newText }
}