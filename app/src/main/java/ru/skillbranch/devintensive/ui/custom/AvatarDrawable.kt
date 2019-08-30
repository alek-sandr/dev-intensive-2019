package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.util.TypedValue
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx

class AvatarDrawable(context: Context, private val text: String, private val size: Int) : Drawable() {
    private val bgRect = Rect()
    private val bgPaint = Paint()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private var mIntrinsicWidth: Int
//    private var mIntrinsicHeight: Int

    init {
        val sizePx = context.dpToPx(size)
        bgRect.set(0, sizePx.toInt(), sizePx.toInt(), 0)

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
        bgPaint.color = value.data
        bgPaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textAlign = Align.CENTER
        textPaint.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        val textSize = sizePx * 0.46f
        textPaint.textSize = textSize
//        mIntrinsicWidth = (textPaint.measureText(text, 0, text.length) + 0.5).toInt()
//        mIntrinsicHeight = textPaint.getFontMetricsInt(null)
//        mIntrinsicWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, context.resources.displayMetrics).toInt()
//        mIntrinsicHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, context.resources.displayMetrics).toInt()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bgRect.set(bounds)
        textPaint.textSize = bgRect.height() * 0.46f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2f, bgPaint)
        canvas.drawText(text, 0, text.length, bounds.exactCenterX(),
            bounds.exactCenterY() - ((textPaint.descent() + textPaint.ascent()) / 2f), textPaint)
    }

    override fun getOpacity(): Int {
        return textPaint.alpha
    }

    override fun getIntrinsicWidth(): Int {
//        return mIntrinsicWidth
        return bgRect.width()
    }

    override fun getIntrinsicHeight(): Int {
//        return mIntrinsicHeight
        return bgRect.height()
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(filter: ColorFilter?) {
        textPaint.colorFilter = filter
    }
}