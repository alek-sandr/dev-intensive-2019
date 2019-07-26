package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Align
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile_constraint.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel


class ProfileActivity : AppCompatActivity() {
    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_constraint)
        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            if (isEditMode) {
                saveProfileInfo()
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Toast.makeText(this@ProfileActivity, "Invalid", Toast.LENGTH_SHORT).show()
                if (!isRepositoryUrlValid()) {
                    wr_repository.error = resources.getString(R.string.repository_error)
                } else {
                    wr_repository.error = null
                }
            }
        })
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                val value = TypedValue()
                context.theme.resolveAttribute(R.attr.colorAccent, value, true)
                val color = value.data
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            } else {
                null
            }
            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }
            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        delegate.setLocalNightMode(mode)
        updateAvatar()
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }
        updateAvatar()
    }

    private fun updateAvatar() {
        viewModel.getProfileData().value?.let {
            val first = Utils.transliteration(it.firstName).trim().firstOrNull()
            val second = Utils.transliteration(it.lastName).trim().firstOrNull()
            val initials = (first ?: "").toString() + (second ?: "").toString()
            if (initials.isNotEmpty()) {
//                val avatarDrawable = AvatarDrawable(this, initials.toUpperCase())
                val avatarDrawable = createAvatar(initials.toUpperCase())
                iv_avatar.setImageBitmap(avatarDrawable)
            } else {
                iv_avatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.avatar_default))
            }
        }
    }

    private fun saveProfileInfo() {
        if (!isRepositoryUrlValid()) {
            et_repository.setText("")
            wr_repository.error = null
        }
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }

    private fun isRepositoryUrlValid(): Boolean {
        if (et_repository.text.isNullOrBlank()) {
            return true
        }
        val text = et_repository.text.toString()
        if (!text.contains("github.com/")) {
            return false
        }
        val parts = text.split("github\\.com/".toRegex())
        if (parts.first().isEmpty() ||
            parts.first().matches("""(www.|http://|https://|http://www.|https://www.)""".toRegex())) {
            if (parts.last().matches("enterprise|features|topics|collections|trending|events|marketplace|pricing|nonprofit|customer-stories|security|login|join".toRegex())) {
                return false
            } else if (parts.last().matches("[\\w-]+\$".toRegex())) {
                return true
            }
        }
        return false
    }

    private fun createAvatar(text: String): Bitmap {
        val bgPaint = Paint()
        val value = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, value, true)
        bgPaint.color = value.data
        bgPaint.style = Paint.Style.FILL

        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics)

        val textPaint = Paint(ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize
        textPaint.color = Color.WHITE
        textPaint.textAlign = Align.CENTER
        textPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, resources.displayMetrics)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, resources.displayMetrics)
        val image = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawRect(0f, 0f, width, height, bgPaint)
        canvas.drawText(text, 0, text.length, width / 2f,
            height / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f), textPaint)
        return image
    }
}
