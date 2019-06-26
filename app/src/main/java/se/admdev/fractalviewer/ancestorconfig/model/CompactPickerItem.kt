package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import android.widget.Button
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class CompactPickerItem<T : Parcelable>(
    val content: T,
    val name: String,
    val decorator: (@RawValue Button.() -> Unit)? = null
) : Parcelable
