package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlin.math.max
import kotlin.math.min

@Parcelize
enum class GroupOperator : Parcelable {
    SUM {
        override val function = { a: List<Int> -> a.fold(0) { acc, v -> acc + v } }
        override val symbol = "Σ"
    },
    PRODUCT {
        override val function = { a: List<Int> -> a.fold(0) { acc, v -> acc * v } }
        override val symbol = "∏"
    },
    TAKE_BIGGEST {
        override val function = { a: List<Int> -> a.fold(Int.MIN_VALUE) { acc, v -> max(acc, v) } }
        override val symbol = "∨"
    },
    TAKE_SMALLEST {
        override val function = { a: List<Int> -> a.fold(Int.MAX_VALUE) { acc, v -> min(acc, v) } }
        override val symbol = "∧"
    };

    @IgnoredOnParcel
    open val function: (List<Int>) -> Int = { _ -> 0 }
    @IgnoredOnParcel
    open val symbol: String = ""
}