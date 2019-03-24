package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ConfigNode(
    val label: Char
) : Parcelable