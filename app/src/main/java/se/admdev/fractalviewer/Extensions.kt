package se.admdev.fractalviewer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.ancestorconfig.model.isReferenceOperand
import kotlin.math.ceil
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

fun Int.toPixel(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
}

fun Int.toDp(context: Context): Float {
    val logicalDensity = context.resources.displayMetrics.density
    return ceil(this / logicalDensity)
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

var View.isVisible
    get() = visibility != View.GONE
    set(value){
        visibility = if(value) View.VISIBLE else View.GONE
    }

fun getDarkTintColorStateList(v: View): ColorStateList = v.resources.getColorStateList(R.color.colorPrimaryDark, null)

fun TextView.showLabel(label: Char?) {
    label?.let {
        text = it.toString()
        backgroundTintList = ColorStateList.valueOf(it.getLabelColor())
    }
}

fun Button.showOperand(op: Operand?) {
    if (op.isReferenceOperand()) {
        op?.label?.let {
            text = it.toString()
            backgroundTintList = ColorStateList.valueOf(it.getLabelColor())
        }
    } else if (op != null) {
        text = op.name
        backgroundTintList = null
    } else {
        text = ""
        backgroundTintList = null
    }
}

fun Button.isNotEmpty(): Boolean = text.isNotEmpty()

fun ViewSwitcher.showList(show: Boolean) {
    val currentlyShowingEmpty = nextView is RecyclerView
    if (show == currentlyShowingEmpty) showNext()
}