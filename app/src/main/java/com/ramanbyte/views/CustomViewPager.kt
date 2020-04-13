package com.ramanbyte.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author Vinay Kumbhar <vinay.pkumbhar></vinay.pkumbhar>@gmail.com>
 * @since 06-04-2020
 */
class CustomViewPager(
    context: Context,
    attrs: AttributeSet?
) : ViewPager(context, attrs) {
    var isSwipeEnabled = true
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onTouchEvent(ev)
        } else isSwipeEnabled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onInterceptTouchEvent(ev)
        } else isSwipeEnabled
    }

    abstract class PagerListener : OnPageChangeListener {
        var size = 0
        var isCircleIndicator = false
        fun setViewPager(size: Int, isCircleIndicator: Boolean) {
            this.size = size
            this.isCircleIndicator = isCircleIndicator
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageSelected(position: Int) {
            onPageSelected(position, size, isCircleIndicator)
        }

        protected abstract fun onPageSelected(
            position: Int,
            size: Int,
            isCircleIndicator: Boolean
        )
    }

}