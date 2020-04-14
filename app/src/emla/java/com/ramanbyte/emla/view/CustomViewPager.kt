package com.ramanbyte.emla.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class CustomViewPager (context: Context, attrs: AttributeSet?) : ViewPager(context,attrs) {

    private var isSwipeEnabled: Boolean = false

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

    fun setSwipeEnabled(swipeEnabled: Boolean) {
        isSwipeEnabled = swipeEnabled
    }

    abstract class PagerListener : ViewPager.OnPageChangeListener {
        internal var size: Int = 0
        internal var isCircleIndicator: Boolean = false

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

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageSelected(position: Int) {
            onPageSelected(position, size, isCircleIndicator)
        }

        protected abstract fun onPageSelected(position: Int, size: Int, isCircleIndicator: Boolean)
    }

}