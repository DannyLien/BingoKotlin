package com.litton.bingokotlin

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class NumberButton(context: Context) : AppCompatButton(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)

    var number: Int = 0
    var picked: Boolean = false
    var pos: Int = 0
}