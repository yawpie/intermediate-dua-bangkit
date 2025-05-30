package com.dicoding.intermediate_satu.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.intermediate_satu.R

class BorderedEditTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs){

    init {
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        this.background = null
        this.background = context.getDrawable(R.drawable.edittext_border)

    }

}