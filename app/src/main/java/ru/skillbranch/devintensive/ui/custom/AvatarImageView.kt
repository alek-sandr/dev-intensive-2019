package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.utils.Utils

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 0
    }
    private var aivBorderColor = DEFAULT_BORDER_COLOR
    private var aivBorderWidth = context.dpToPx(DEFAULT_BORDER_WIDTH)
//    private var aivInitials = "??"
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView)
            aivBorderColor = a.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            aivBorderWidth = a.getDimension(R.styleable.AvatarImageView_aiv_borderWidth, context.dpToPx(DEFAULT_BORDER_WIDTH))
            a.recycle()
        }
        borderPaint.color = aivBorderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = aivBorderWidth
    }

    fun setInitials(initials: String) {
        setImageDrawable(Utils.generateAvatar(context, 40, initials))
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawOval(0f, 0f, height.toFloat(), width.toFloat(), bitmapPaint)
        if (borderPaint.strokeWidth > 0f) {
            canvas.drawCircle(width / 2f, width / 2f, width / 2f - borderPaint.strokeWidth / 2f, borderPaint)
        }
    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//
//        setImageBitmap(createAvatar(aivInitials))
//    }

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

    private fun setupBitmap() {
        val mBitmap = getBitmapFromDrawable(drawable)
        mBitmap?.let { bitmap ->
            bitmapPaint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
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
}