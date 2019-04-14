package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Operand(val name: String, val label: Char? = null) : Parcelable {
    constructor(node: ConfigNode) : this(node.label.toString(), node.label)
    constructor(text: String) : this(text, text.toCharOrNull())
}

private fun String.toCharOrNull(): Char? {
    val c = this.singleOrNull()
    return if (c in 'a'..'z' || c in 'A'..'Z'){
        c
    } else {
        this.toInt() // throw exception if not number
        null
    }
}

fun Operand?.isReferenceOperand(): Boolean = this?.label != null
