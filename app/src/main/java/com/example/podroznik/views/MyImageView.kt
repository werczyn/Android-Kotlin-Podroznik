package com.example.podroznik.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextPaint
import android.util.AttributeSet

class MyImageView(context : Context, attrs : AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    var date = ""
    var city = ""
    var country = ""
    val paint = TextPaint().apply {
        color = Color.RED
        textSize = 50f
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawText("$country, $city, $date", 0f, (height.toFloat()/2)-paint.textSize,paint);
    }
}