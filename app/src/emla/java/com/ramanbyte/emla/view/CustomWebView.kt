package com.ramanbyte.emla.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.ramanbyte.R
import com.ramanbyte.utilities.AppLog


class CustomWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init {
        init(context, attrs, defStyleAttr)
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

}