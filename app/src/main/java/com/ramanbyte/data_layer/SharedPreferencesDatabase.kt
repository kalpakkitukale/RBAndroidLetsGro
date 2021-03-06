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
    val KEY_START_QUIZ_DATE_TIME = "START_DATE_TIME"

    val USER_ID = "userId"
    val KEY_IS_NEW_TOKEN_UPDATED = "KEY_IS_NEW_TOKEN_UPDATED"
    val KEY_DEVICE_INSTANCE_ID = "DEVICE_INSTANCE_ID"
    val KEY_IS_DEVICE_TOKEN_SET = "IS_DEVICE_TOKEN_SET"

    val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    val KEY_TOKEN_DATE_TIME = "TOKEN_DATE_TIME"

    val KEY_CAMPUS_ID = "CAMPUS_ID"
    val KEY_CLIENT_NAME = "CLIENT_NAME"
    val KEY_AWS_S3_ENABLE = "ENABLE_AWS_S3"
    val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    val IS_PERMISSION_DENIED = "isPermissionDenied"
    val PRE_ASSESSMENT_STATUS = "PRE_ASSESSMENT_STATUS"

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