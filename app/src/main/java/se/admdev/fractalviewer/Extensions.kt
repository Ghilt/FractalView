package se.admdev.fractalviewer

import android.view.View

val Boolean?.viewVisibility
        get() = if (this == true) View.VISIBLE else View.GONE