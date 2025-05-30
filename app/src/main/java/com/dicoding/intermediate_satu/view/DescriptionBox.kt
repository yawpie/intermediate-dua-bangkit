package com.dicoding.intermediate_satu.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.intermediate_satu.R

class DescriptionBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val hintPaint: Paint = Paint()


    init {
        hintPaint.color = currentHintTextColor
        hintPaint.textSize = textSize
        hintPaint.isAntiAlias = true
        background = context.getDrawable(R.drawable.edittext_border)
//        textAlignment = View.
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Description"
//        val text = text.toString()
//        if (text.isEmpty()) {
//            canvas.drawText(hint.toString(), 0f, hintPaint.textSize, hintPaint)
//        }
    }
}