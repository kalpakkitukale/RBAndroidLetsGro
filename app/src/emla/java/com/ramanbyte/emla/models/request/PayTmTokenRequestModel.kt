package com.ramanbyte.emla.models.request

import com.ramanbyte.BuildConfig

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 18/3/21
 */
class PayTmTokenRequestModel {
    var mid: String? = ""
    var orderid: String? = ""
    var currency: String? = "INR"
    var custId: String? = ""
    var requestType: String? = "PAYMENT"
    var websiteName: String? = "APPSTAGING"
        get() {
            return if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "uat")
                "DEFAULT"
            else
                "APPSTAGING"
        }
    var callBackURL: String? = ""
    var amount: String? = "0.0"
    var isStaging: Boolean = !(BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "uat")
}