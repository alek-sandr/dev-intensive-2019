package ru.skillbranch.devintensive.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import java.lang.StringBuilder

object Utils {
    private val symbolsMap = mapOf(
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya"
    )

    fun parseFullName(fullName: String?) : Pair<String?, String?> {
        val parts = if (fullName?.trim() == "") {
            null
        } else {
            fullName?.split(" ")
        }
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val builder = StringBuilder()
        payload.forEach {
            if (it == ' ') { // check divider
                if (divider != " ") {
                    builder.append(divider)
                } else {
                    builder.append(" ")
                }
            } else { //check symbol
                var c: String? = symbolsMap[it]
                if (c != null) { // found match
                    builder.append(c)
                } else {
                    c = symbolsMap[it.toLowerCase()] // maybe it in upper case
                    if (c != null) {
                        builder.append(c.capitalize())
                    } else { // no matches append as is
                        builder.append(it)
                    }
                }
            }
        }
        return builder.toString()
    }

    fun toInitials(firstName: String?, lastName: String?) : String? {
        val first = firstName?.trim()?.getOrNull(0) ?: ""
        val second = lastName?.trim()?.getOrNull(0) ?: ""
        val nick = "$first$second"
        return if (nick.isEmpty()) null else nick.toUpperCase()
    }

    fun generateAvatar(context: Context, @Dimension size: Int, initials: String): Drawable {
        val bgPaint = Paint()
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
        bgPaint.color = value.data
        bgPaint.style = Paint.Style.FILL

        val textSize = context.dpToPx((size * 0.5f).toInt())
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        val width = context.dpToPx(size)
        val image = Bitmap.createBitmap(width.toInt(), width.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawCircle(width / 2f, width / 2f, width / 2f, bgPaint)
        canvas.drawText(initials, 0, initials.length, width / 2f,
            width / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f), textPaint)
        return BitmapDrawable(context.resources, image)
    }
}