package se.admdev.fractalviewer

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

val Boolean?.viewVisibility
    get() = if (this == true) View.VISIBLE else View.GONE

fun Button.setTextIfNotNull(newText: String?){
    newText?.let { text = newText }
}

val RecyclerView.gridLayoutManager
    get() = layoutManager as GridLayoutManager