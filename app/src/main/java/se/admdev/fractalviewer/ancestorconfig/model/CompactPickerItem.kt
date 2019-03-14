package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import android.widget.TextView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class CompactPickerItem<T : Parcelable>(
    val content: T,
    val name: String,
    val decorator: (@RawValue TextView.() -> Unit)? = null
) : Parcelable
