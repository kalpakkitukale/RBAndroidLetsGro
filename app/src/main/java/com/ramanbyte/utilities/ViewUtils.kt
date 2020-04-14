package com.ramanbyte.utilities

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.ramanbyte.R
import com.ramanbyte.views.RoundedTextDrawable


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
class ViewUtils {

    companion object {
        fun getCharacterDrawable(character: String): Drawable? {
            return RoundedTextDrawable.builder()
                .beginConfig()
                .textColor(BindingUtils.color(R.color.colorWhite))
                .width(BindingUtils.dimen(R.dimen.dp_43).toInt())
                .height(BindingUtils.dimen(R.dimen.dp_43).toInt())
                .endConfig()
                .round()
                .build(
                    character.toUpperCase(),
                    StaticMethodUtilitiesKtx.getRandomColors()
                )
        }
    }
}

fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun View.setMarginTop(marginTop: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = menuLayoutParams
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}