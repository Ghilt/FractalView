package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class CompactPickerItem<T : Parcelable>(val content: T, val presentFunction: @RawValue T.() -> String) : Parcelable
