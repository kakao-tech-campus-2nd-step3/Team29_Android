package com.iguana.notetaking.util

import android.util.Log
import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
    Log.d("UI Debug", "View ${this.id} is now VISIBLE")
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}