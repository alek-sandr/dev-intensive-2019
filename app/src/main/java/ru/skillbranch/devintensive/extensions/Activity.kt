package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
//    view.clearFocus()
}

fun Activity.isKeyboardOpen() : Boolean {
    val SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128
    val activityRootView = (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
    val r = Rect()
    //r will be populated with the coordinates of your view that area still visible.
    activityRootView.getWindowVisibleDisplayFrame(r)
    val dm = resources.displayMetrics
    val heightDiff = activityRootView.bottom - r.bottom
    return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density
}

fun Activity.isKeyboardClosed() = !isKeyboardOpen()