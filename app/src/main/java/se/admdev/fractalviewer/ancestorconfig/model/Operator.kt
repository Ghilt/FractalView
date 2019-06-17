package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.R
import kotlin.math.pow

@Parcelize
enum class Operator : Parcelable, OperatorData {
    ADDITION {
        override val function = { a: Int, b: Int -> a + b }
        override val symbol = "+"
        override val description = R.string.operator_description_addition
    },
    SUBTRACTION {
        override val function = { a: Int, b: Int -> a - b }
        override val symbol = "-"
        override val description = R.string.operator_description_subtraction
    },
    MULTIPLICATION {
        override val function = { a: Int, b: Int -> a * b }
        override val symbol = "*"
        override val description = R.string.operator_description_multiplication
    },
    DIVISION {
        override val function = { a: Int, b: Int -> a / b }
        override val symbol = "\u005E"
        override val description = R.string.operator_description_division
    },
    EXPONENT {
        override val function = { a: Int, b: Int -> a.toDouble().pow(b).toInt() }
        override val symbol = "\u00F7"
        override val description = R.string.operator_description_exponent
    },
    MODULO {
        override val function = { a: Int, b: Int -> a % b }
        override val symbol = "%"
        override val description = R.string.operator_description_modulo
    },
    EQUALS {
        override val function = { a: Int, b: Int -> if (a == b) 1 else 0 }
        override val symbol = "="
        override val description = R.string.operator_description_equals
    },
    NOT_EQUALS {
        override val function = { a: Int, b: Int -> if (a != b) 1 else 0 }
        override val symbol = "≠"
        override val description = R.string.operator_description_not_equal
    },
    ALMOST_EQUALS {
        override val function = { a: Int, b: Int -> if ((a != 0 && b != 0) || a == 0 && b == 0) 1 else 0 }
        override val symbol = "≈"
        override val description = R.string.operator_description_almost_equals
    },
    NOT_ALMOST_EQUALS {
        override val function = { a: Int, b: Int -> if ((a == 0 && b != 0) || a != 0 && b == 0) 1 else 0 }
        override val symbol = "!≈"
        override val description = R.string.operator_description_not_almpst_equals
    },
    LESS_THAN {
        override val function = { a: Int, b: Int -> if (a < b) 1 else 0 }
        override val symbol = "<"
        override val description = R.string.operator_description_less_than
    },
    GREATER_THAN {
        override val function = { a: Int, b: Int -> if (a > b) 1 else 0 }
        override val symbol = ">"
        override val description = R.string.operator_description_greater_than
    },
    LESS_EQUAL_THAN {
        override val function = { a: Int, b: Int -> if (a <= b) 1 else 0 }
        override val symbol = "<="
        override val description = R.string.operator_description_less_equal_than
    },
    GREATER_EQUAL_THAN {
        override val function = { a: Int, b: Int -> if (a >= b) 1 else 0 }
        override val symbol = ">="
        override val description = R.string.operator_description_greater_equal_than
    },
    AND {
        override val function = { a: Int, b: Int -> if (a != 0 && b != 0) 1 else 0 }
        override val symbol = "&&"
        override val description = R.string.operator_description_and
    },
    OR {
        override val function = { a: Int, b: Int -> if (a != 0 || b != 0) 1 else 0 }
        override val symbol = "||"
        override val description = R.string.operator_description_or
    },
    XOR {
        override val function = { a: Int, b: Int -> if ((a != 0 || b != 0) && (a == 0 || b == 0)) 1 else 0 }
        override val symbol = "xor"
        override val description = R.string.operator_description_xor
    };

    @IgnoredOnParcel
    open val function: (Int, Int) -> Int = { _, _ -> 0 }

    companion object {
        val MATHEMATICAL_OPERATORS = listOf(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EXPONENT, MODULO)
    }
}