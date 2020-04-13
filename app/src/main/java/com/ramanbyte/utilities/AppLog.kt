package com.ramanbyte.utilities

import android.util.Log
import com.ramanbyte.R

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
object AppLog {

    fun infoLog(strMsg: String) {
        try {
            Log.i(BindingUtils.string(R.string.app_name_tag), strMsg)
        } catch (e: Exception) {
            e.printStackTrace()
            errorLog(e)
        }

    }

    fun warningLog(strMsg: String) {
        try {
            Log.w(BindingUtils.string(R.string.app_name_tag), strMsg)
        } catch (e: Exception) {
            e.printStackTrace()
            errorLog(e)
        }

    }

    fun debugLog(strMsg: String) {
        try {
            Log.d(BindingUtils.string(R.string.app_name_tag), strMsg)
        } catch (e: Exception) {
            e.printStackTrace()
            errorLog(e)
        }

    }

    fun verboseLog(strMsg: String) {
        try {
            Log.v(BindingUtils.string(R.string.app_name_tag), strMsg)
        } catch (e: Exception) {
            e.printStackTrace()
            errorLog(e)
        }

    }

    fun errorLog(strMsg: String) {
        try {
            Log.e(BindingUtils.string(R.string.app_name_tag), " | Error Message : $strMsg")
        } catch (e: Exception) {
            e.printStackTrace()
            errorLog(e)
        }

    }

    fun errorLog(e: Throwable) {


    }

    fun errorLog(strMsg: String?, e: Throwable) {
        var strMsg = strMsg
        if (strMsg == null) {
            strMsg = "Null message"
        }
        errorLog(strMsg)
    }
}
