package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.util.TypedValue
import ru.skillbranch.devintensive.R

class AvatarDrawable(context: Context, private val text: String) : Drawable() {
    companion object {
        private const val DEFAULT_TEXTSIZE = 48f
    }
    private val bgRect = Rect()
    private val bgPaint = Paint()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mIntrinsicWidth: Int
    private var mIntrinsicHeight: Int

    init {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
//        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
        bgPaint.color = value.data
//        } else {
//            bgPaint.color = ContextCompat.getColor(context, R.color.color_accent)
//        }

        bgPaint.style = Paint.Style.FILL
        textPaint.color = Color.WHITE
        textPaint.textAlign = Align.CENTER
        textPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXTSIZE, context.resources.displayMetrics)
        textPaint.textSize = textSize
//        mIntrinsicWidth = (textPaint.measureText(text, 0, text.length) + 0.5).toInt()
//        mIntrinsicHeight = textPaint.getFontMetricsInt(null)
        mIntrinsicWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, context.resources.displayMetrics).toInt()
        mIntrinsicHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, context.resources.displayMetrics).toInt()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bgRect.set(bounds)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bgRect, bgPaint)
        canvas.drawText(text, 0, text.length, bounds.centerX().toFloat(),
            bounds.centerY().toFloat() - ((textPaint.descent() + textPaint.ascent()) / 2f), textPaint)
    }

    override fun getOpacity(): Int {
        return textPaint.alpha
    }

    override fun getIntrinsicWidth(): Int {
        return mIntrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mIntrinsicHeight
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(filter: ColorFilter?) {
        textPaint.colorFilter = filter
    }
}