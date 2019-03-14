package se.admdev.fractalviewer

import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

val Boolean?.viewVisibility
    get() = if (this == true) View.VISIBLE else View.GONE

fun Button.setTextIfNotNull(newText: String?) {
    newText?.let { text = newText }
}

val RecyclerView.gridLayoutManager
    get() = layoutManager as GridLayoutManager

fun Char.getLabelColor(): Int {
    val ran = Random(this.toInt())
    return Color.argb(1f, ran.nextFloat(), ran.nextFloat(), ran.nextFloat())
}