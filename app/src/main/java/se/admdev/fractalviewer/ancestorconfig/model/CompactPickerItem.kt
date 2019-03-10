package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CompactPickerItem<T : Parcelable>(val content: T) : Parcelable
