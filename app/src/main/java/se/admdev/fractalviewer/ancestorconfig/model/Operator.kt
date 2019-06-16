package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlin.math.pow

@Parcelize
enum class Operator : Parcelable, OperatorData {
    ADDITION {
        override val function = { a: Int, b: Int -> a + b }
        override val symbol = "+"
    },
    SUBTRACTION {
        override val function = { a: Int, b: Int -> a - b }
        override val symbol = "-"
    },
    MULTIPLICATION {
        override val function = { a: Int, b: Int -> a * b }
        override val symbol = "*"
    },
    DIVISION {
        override val function = { a: Int, b: Int -> a / b }
        override val symbol = "\u005E"
    },
    EXPONENT {
        override val function = { a: Int, b: Int -> a.toDouble().pow(b).toInt() }
        override val symbol = "\u00F7"
    },
    MODULO {
        override val function = { a: Int, b: Int -> a % b }
        override val symbol = "%"
    },
    EQUALS {
        override val function = { a: Int, b: Int -> if (a == b) 1 else 0 }
        override val symbol = "="
    },
    NOT_EQUALS {
        override val function = { a: Int, b: Int -> if (a != b) 1 else 0 }
        override val symbol = "≠"
    },
    ALMOST_EQUALS {
        override val function = { a: Int, b: Int -> if ((a != 0 && b != 0) || a == 0 && b == 0) 1 else 0 }
        override val symbol = "≈"
    },
    NOT_ALMOST_EQUALS {
        override val function = { a: Int, b: Int -> if ((a == 0 && b != 0) || a != 0 && b == 0) 1 else 0 }
        override val symbol = "!≈"
    },
    LESS_THAN {
        override val function = { a: Int, b: Int -> if (a < b) 1 else 0 }
        override val symbol = "<"
    },
    GREATER_THAN {
        override val function = { a: Int, b: Int -> if (a > b) 1 else 0 }
        override val symbol = ">"
    },
    LESS_EQUAL_THAN {
        override val function = { a: Int, b: Int -> if (a <= b) 1 else 0 }
        override val symbol = "<="
    },
    GREATER_EQUAL_THAN {
        override val function = { a: Int, b: Int -> if (a >= b) 1 else 0 }
        override val symbol = ">="
    },
    AND {
        override val function = { a: Int, b: Int -> if (a > 0 && b > 0) 1 else 0 }
        override val symbol = "&&"
    },
    OR {
        override val function = { a: Int, b: Int -> if (a > 0 || b > 0) 1 else 0 }
        override val symbol = "||"
    },
    XOR {
        override val function = { a: Int, b: Int -> if ((a != 0 || b != 0) && (a == 0 || b == 0)) 1 else 0 }
        override val symbol = "xor"
    };

    @IgnoredOnParcel
    open val function: (Int, Int) -> Int = { _, _ -> 0 }

    companion object {
        val MATHEMATICAL_OPERATORS = listOf(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EXPONENT, MODULO)
    }
}