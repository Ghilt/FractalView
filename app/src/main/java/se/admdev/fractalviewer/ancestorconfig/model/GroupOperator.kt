package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.R
import kotlin.math.max
import kotlin.math.min

@Parcelize
enum class GroupOperator : Parcelable, OperatorData {
    SUM {
        override val function = { a: List<Int> -> a.fold(0) { acc, v -> acc + v } }
        override val symbol = "Σ"
        override val description = R.string.operator_description_sum
    },
    PRODUCT {
        override val function = { a: List<Int> -> a.fold(0) { acc, v -> acc * v } }
        override val symbol = "∏"
        override val description = R.string.operator_description_product
    },
    TAKE_MAXIMUM {
        override val function = { a: List<Int> -> a.fold(Int.MIN_VALUE) { acc, v -> max(acc, v) } }
        override val symbol = "∨"
        override val description = R.string.operator_description_max
    },
    TAKE_MINIMUM {
        override val function = { a: List<Int> -> a.fold(Int.MAX_VALUE) { acc, v -> min(acc, v) } }
        override val symbol = "∧"
        override val description = R.string.operator_description_min
    };

    @IgnoredOnParcel
    open val function: (List<Int>) -> Int = { _ -> 0 }
}