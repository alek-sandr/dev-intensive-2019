package ru.skillbranch.devintensive.extensions

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.devintensive.R

fun Snackbar.applyAppStyle(): Snackbar {
    val text = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    text.setTextColor(ContextCompat.getColor(context, R.color.color_snackbar_text))
    setActionTextColor(ContextCompat.getColor(context, R.color.color_snackbar_action))
    view.background = ContextCompat.getDrawable(context, R.drawable.snackbar_bg)
    return this
}