package com.verve.vps.animations.tabs

import android.view.View
import androidx.viewpager.widget.ViewPager


class FanTransformation : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {

        page.translationX = -position * page.getWidth()
        page.pivotX = 0f
        page.pivotY = page.getHeight().toFloat() / 2
        page.cameraDistance = 20000f

        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            position <= 0 -> {    // [-1,0]
                page.alpha = 1f
                page.rotationY = -120 * Math.abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1f
                page.rotationY = 120 * Math.abs(position)

            }
            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
        }


    }
}