package com.ramanbyte.utilities

import android.os.Build
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate


/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 23/12/19
 */
object ThemeHelper {
    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    private const val BATTERY_SAVER_MODE = "battery"
    val DEFAULT_MODE = "default"

    fun applyTheme(@NonNull themePref: String?) {
        when (themePref) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            BATTERY_SAVER_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)

            DEFAULT_MODE -> {
                //set default dark mode below android 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Above android version P(API 28)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
}