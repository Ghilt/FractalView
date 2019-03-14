package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Operand(val name: String, val label: Char? = null) : Parcelable