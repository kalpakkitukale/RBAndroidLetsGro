package com.ramanbyte.emla.models.request

import com.google.gson.annotations.SerializedName
import com.ramanbyte.utilities.*

class InsertTransactionRequestModel {

    var id: Int = 0 // 1st time it will 0 then send id for update transaction status
    var userId: Int = 0
    var transId: Long = 0
    var transDate: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var transType: String = KEY_APPLICATION_FORM_TRANSACTION_TYPE
    var amountPaid: String = KEY_BLANK
    var paymentMethod: String = KEY_BLANK
    var penalty: Int = 0
    var paymentDomain: String = keyPaymentDomain
    var paymentGateway = KEY_BLANK
    var paymentDescription = KEY_BLANK
    var transactionStatus: String = KEY_BLANK
    var flag: String = KEY_BLANK
    var isMailSent: Boolean = DEL_STATUS1
    var added_By: Int = 0
    var registrationId: Int = 0
    var clientName = ""

    @SerializedName("createdBy")
    var createdBy: Int = 0
    var createdDate: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var modifyBy: Int = 0
    var modifyDate: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var appStatus: String = APP_STATUS
    var delStatus: Boolean = DEL_STATUS1
    var ipAddress: String = IpUtility.getIpAddress()
    var deviceId: Int = 0
    var deviceType: String? = KEY_ANDROID

    /**
     * Cart Courses Fee Details
     */
    var fees = ArrayList<CourseFeeRequestModel>()
}