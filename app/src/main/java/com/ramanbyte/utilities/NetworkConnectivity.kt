package com.ramanbyte.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.ramanbyte.BaseAppController

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
object NetworkConnectivity {

    fun isConnectedToInternet(): Boolean {
        try {
            val connMgr =
                BaseAppController.applicationInstance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnected) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    internetBandWidth();
                }*/
                isConnectionFast(networkInfo.type, networkInfo.subtype)
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            return false
        }
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    private fun isConnectionFast(type: Int, subType: Int): Boolean {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            AppLog.infoLog("Connection SubType ---- $subType")
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT // ~ 50-100 kbps
                    , TelephonyManager.NETWORK_TYPE_CDMA // ~ 14-64 kbps
                -> return false
                TelephonyManager.NETWORK_TYPE_EDGE // ~ 50-100 kbps
                    , TelephonyManager.NETWORK_TYPE_GPRS // ~ 100 kbps
                    ,

                TelephonyManager.NETWORK_TYPE_EVDO_0 // ~ 400-1000 kbps
                    , TelephonyManager.NETWORK_TYPE_EVDO_A // ~ 600-1400 kbps
                    , TelephonyManager.NETWORK_TYPE_HSDPA // ~ 2-14 Mbps
                    , TelephonyManager.NETWORK_TYPE_HSPA // ~ 700-1700 kbps
                    , TelephonyManager.NETWORK_TYPE_HSUPA // ~ 1-23 Mbps
                    , TelephonyManager.NETWORK_TYPE_UMTS // ~ 400-7000 kbps
                    ,
                    /*
                     * Above API level 7, make sure to set android:targetSdkVersion
                     * to appropriate level to use these
                     */
                TelephonyManager.NETWORK_TYPE_EHRPD // API level 11  // ~ 1-2 Mbps
                    , TelephonyManager.NETWORK_TYPE_EVDO_B // API level 9 // ~ 5 Mbps
                    , TelephonyManager.NETWORK_TYPE_HSPAP // API level 13 // ~ 10-20 Mbps
                    , TelephonyManager.NETWORK_TYPE_LTE // API level 11 // ~ 10+ Mbps
                    , TelephonyManager.NETWORK_TYPE_IDEN // API level 8 // ~25 kbps
                    ,
                    // Unknown
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> return true
                else -> return true
            }
        } else {
            return false
        }
    }
}