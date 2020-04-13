package com.ramanbyte.utilities

import java.net.NetworkInterface
import java.util.*


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 26/02/2020
 */
class IpUtility {
    companion object {
        fun getIpAddress(): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (i in 0 until interfaces.size) {
                    val addrs = Collections.list(interfaces[i].inetAddresses)
                    for (i in 0 until addrs.size) {
                        if (!addrs[i].isLoopbackAddress) {
                            val sAddr = addrs[i].hostAddress
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (isIPv4)
                                return sAddr

                        }
                    }
                }
            } catch (throwable: Throwable) {
                AppLog.infoLog("IP Address  not found error = " + throwable.message)
            }
            return ""
        }
    }
}