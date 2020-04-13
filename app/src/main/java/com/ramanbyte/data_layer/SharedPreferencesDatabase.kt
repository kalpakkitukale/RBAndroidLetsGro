package com.ramanbyte.data_layer

import android.content.Context
import android.content.SharedPreferences
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import java.util.*

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 26/02/2020
 */
object SharedPreferencesDatabase {

    val KEY_DEVICE_IMEI = "DEVICE_IMEI_NUMBER"
    val KEY_THEME = "prefs.theme"

    private val prefEditor: SharedPreferences.Editor?
        get() {
            try {
                val preferences = Objects.requireNonNull(BaseAppController.applicationInstance)
                    ?.getSharedPreferences(
                        BindingUtils.string(R.string.app_name),
                        Context.MODE_PRIVATE
                    )
                return preferences?.edit()
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                return null
            }
        }

    @JvmStatic
    private val pref: SharedPreferences?
        get() {
            try {
                return Objects.requireNonNull(BaseAppController.applicationInstance)
                    ?.getSharedPreferences(
                        BindingUtils.string(R.string.app_name),
                        Context.MODE_PRIVATE
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                return null
            }
        }

    fun setStringPref(key: String, value: String) {
        try {
            val editor = prefEditor
            editor!!.putString(key, value)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    fun getStringPref(key: String): String {
        return try {
            pref?.getString(key, "")?:""
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ""
        }
    }

    fun setBoolPref(key: String, value: Boolean) {
        try {
            val editor = prefEditor!!
            editor.putBoolean(key, value)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    fun getBoolPref(key: String): Boolean {
        try {
            return Objects.requireNonNull(pref)!!.getBoolean(key, false)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            return false
        }
    }

    fun setIntPref(key: String, value: Int) {
        try {
            val editor = prefEditor!!
            editor.putInt(key, value)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    fun getIntPref(key: String): Int {
        try {
            return Objects.requireNonNull(pref)!!.getInt(key, -1)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            return -1
        }
    }



}