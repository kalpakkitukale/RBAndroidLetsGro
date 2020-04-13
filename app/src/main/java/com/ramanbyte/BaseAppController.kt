package com.ramanbyte

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.data_layer.SharedPreferencesDatabase.KEY_THEME
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.ThemeHelper

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
abstract class BaseAppController : Application() {

    //abstract var mFirebaseRemoteConfig: FirebaseRemoteConfig

    companion object {

        //val applicationInstance
        @get:Synchronized
        var applicationInstance: BaseAppController? = null
            private set

        @get:Synchronized
        var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null


        fun setEnterPageAnimation(activity: Activity) {
            try {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun setExitPageAnimation(activity: Activity) {
            try {
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }

        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        /**Vinay K
         * Theme change on dark and night mode*/
        val themePref: String? = SharedPreferencesDatabase.getStringPref(KEY_THEME)
        themePref?.let { SharedPreferencesDatabase.setStringPref(KEY_THEME, it) }
        ThemeHelper.applyTheme(if(themePref?.isEmpty()==true) ThemeHelper.DEFAULT_MODE else themePref)
    }


}