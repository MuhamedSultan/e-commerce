package com.example.e_commerce_app.util

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        when {
            position < -1 -> {
                view.alpha = 0f
            }

            position <= 1 -> {
                view.alpha = 1 - Math.abs(position)
                view.scaleY = 0.85f + (1 - Math.abs(position)) * 0.15f
            }

            else -> {
                view.alpha = 0f
            }
        }
    }
}
