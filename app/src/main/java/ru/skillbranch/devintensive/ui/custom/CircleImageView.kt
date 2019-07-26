package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ImageView(context, attrs) {

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
    }

    private var borderColor: Int = DEFAULT_BORDER_COLOR
    private var borderWidthDp: Int = DEFAULT_BORDER_WIDTH
    private val mShaderMatrix = Matrix()
    private val mBitmapDrawBounds = RectF()
    private val mStrokeBounds = RectF()
    private var mBitmap: Bitmap? = null
    private val mBitmapPaint = Paint(ANTI_ALIAS_FLAG)
    private val mBorderPaint= Paint(ANTI_ALIAS_FLAG)
    private var mInitialized = false

    init {
        var bWidth: Float? = null

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            bWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, dpToFloat(DEFAULT_BORDER_WIDTH))
            a.recycle()
        }

        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = bWidth ?: dpToFloat(DEFAULT_BORDER_WIDTH)
        mInitialized = true
        setupBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mBorderPaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawBorder(canvas)
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(context, colorId)
        mBorderPaint.color = borderColor
        invalidate()
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        mBorderPaint.color = borderColor
        invalidate()
    }

    @Dimension
    fun getBorderWidth(): Int {
        return borderWidthDp
    }

    fun setBorderWidth(dp: Int) {
        borderWidthDp = dp
        mBorderPaint.strokeWidth = dpToFloat(dp)
        invalidate()
    }

    private fun drawBorder(canvas: Canvas) {
        if (mBorderPaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mBorderPaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }
        mBitmap = getBitmapFromDrawable(drawable)
        mBitmap?.let { bitmap ->
            mBitmapPaint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            updateBitmapSize()
        }
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun updateBitmapSize() {
        mBitmap?.let {
            val dx: Float
            val dy: Float
            val scale: Float

            // scale up/down with respect to this view size and maintain aspect ratio
            // translate bitmap position with dx/dy to the center of the image
            if (it.width < it.height) {
                scale = mBitmapDrawBounds.width() / it.width.toFloat()
                dx = mBitmapDrawBounds.left
                dy = mBitmapDrawBounds.top - it.height * scale / 2f + mBitmapDrawBounds.width() / 2f
            } else {
                scale = mBitmapDrawBounds.height() / it.height.toFloat()
                dx = mBitmapDrawBounds.left - it.width * scale / 2f + mBitmapDrawBounds.width() / 2f
                dy = mBitmapDrawBounds.top
            }
            mShaderMatrix.setScale(scale, scale)
            mShaderMatrix.postTranslate(dx, dy)
            mBitmapPaint.shader?.setLocalMatrix(mShaderMatrix)
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun dpToFloat(dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics)
    }
}