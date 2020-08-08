package com.ramanbyte.emla.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import androidx.core.view.MotionEventCompat
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.ramanbyte.R
import com.ramanbyte.utilities.AppLog


class CustomWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), NestedScrollingChild {

    private var mLastY: Int = 0
    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mNestedOffsetY: Int = 0
    private val mChildHelper: NestedScrollingChildHelper

    init {
        init(context, attrs, defStyleAttr)
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    var backgroundColour: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }


    @SuppressLint("PrivateApi")
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.CustomWebView, defStyleAttr, 0)

        backgroundColour =
            attributes.getColor(R.styleable.CustomWebView_cwv_background_color, backgroundColour)

        this.settings.javaScriptEnabled = true
        this.isVerticalScrollBarEnabled = true
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val nightModeFlags =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            AppLog.infoLog("=====@@${WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)}")

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(
                    this.settings,
                    WebSettingsCompat.FORCE_DARK_ON
                )
            }
            this.setBackgroundColor(backgroundColour)
        } else {
            this.setBackgroundColor(backgroundColour)

        }
        attributes.recycle()
    }

    /*************************************************  For scroll ***************************************/


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        var returnValue = false

        val event = MotionEvent.obtain(ev)
        val action = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0
        }
        val eventY = event.y.toInt()
        event.offsetLocation(0f, mNestedOffsetY.toFloat())
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                var totalScrollOffset = 0
                var deltaY = mLastY - eventY
                // NestedPreScroll
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    totalScrollOffset += mScrollOffset[1]
                    deltaY -= mScrollConsumed[1]
                    event.offsetLocation(0f, (-mScrollOffset[1]).toFloat())
                    mNestedOffsetY += mScrollOffset[1]
                }
                returnValue = super.onTouchEvent(event)

                // NestedScroll
                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    totalScrollOffset += mScrollOffset[1]
                    event.offsetLocation(0f, mScrollOffset[1].toFloat())
                    mNestedOffsetY += mScrollOffset[1]
                    mLastY -= mScrollOffset[1]
                }
                mLastY = eventY - totalScrollOffset
            }
            MotionEvent.ACTION_DOWN -> {
                returnValue = super.onTouchEvent(event)
                mLastY = eventY
                // start NestedScroll
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                returnValue = super.onTouchEvent(event)
                // end NestedScroll
                stopNestedScroll()
            }
        }
        return returnValue
    }

    // Nested Scroll implements
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }


}