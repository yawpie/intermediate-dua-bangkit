package com.dicoding.intermediate_satu.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.intermediate_satu.R

class PasswordInputField @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatEditText(context, attrs) {

    private var isPasswordVisible = false
    private var errorCondition = false

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PasswordView,
            0,
            0
        )
        val minimumChar = typedArray.getInt(R.styleable.PasswordView_minimumChar, 8)
        setup(minimumChar)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setup(minimumChar: Int) {

        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        updatePasswordVisibilityIcon()

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().length < minimumChar) {
                    error = "Password has to be at least $minimumChar characters"
                } else {
                    error= null
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setSelection(text?.length ?: 0) // Keep the cursor at the end
        updatePasswordVisibilityIcon()
    }

    private fun updatePasswordVisibilityIcon() {
        val icon = if (isPasswordVisible) {
            R.drawable.visibility_24px // Visible password icon
        } else {
            R.drawable.visibility_off_24px // Hidden password icon
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(context, icon),
            null
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password"
    }



}