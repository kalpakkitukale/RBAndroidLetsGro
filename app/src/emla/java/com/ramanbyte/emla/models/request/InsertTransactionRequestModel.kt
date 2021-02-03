package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

class InsertTransactionRequestModel {

    var id: Int = 0 // 1st time it will 0 then send id for update transaction status
    var tran_Date: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var user_Id: Int = 0
    var app_Id: Int = 0
    var tran_Id: String = "0" //StaticHelpers.Constants.KEY_BLANK
    var tran_Type: String = KEY_APPLICATION_FORM_TRANSACTION_TYPE
    var amount_Paid: String = KEY_BLANK
    var paymentMethod: String = KEY_BLANK
    var isMailSent: String = KEY_N
    var chequeNo: String = "0"
    var bankName: String = KEY_BLANK
    var branchName: String = KEY_BLANK
    var chequeDate: String = DateUtils.getCurDate(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)!!
    var paymentDomain: String = keyPaymentDomain
    var flag: String = KEY_BLANK
    var added_By: Int = 0
    var penalty: Int = 0
    var createdDate: String = KEY_BLANK
    var modifyDate: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var created_By: Int = 0
    var modify_By: Int = 0
    var app_Status: String = KEY_APP_STATUS
    var del_Status: String = KEY_DEL_STATUS
    var ipAddress: String = IpUtility.getIpAddress()
    var deviceId: Int = 0
    var transactionStatus: String = KEY_BLANK
    var paymentType: String =KEY_MOBILE_PAYMENT_TYPE
    var totalCount: Int = 0
    var campusId: Int = 0
    var clientId: Int = 0
    var registrationId: Int = 0
    var programId: Int = 0
    var academicYearId: Int = 0
    var admissionYearId: Int = 0

    var studentName = ""
    var emailId = ""
    var paymentStepIntegration = ""
    var paymentGateway = KEY_BLANK
    var programName = KEY_BLANK
    var paymentDescription = KEY_BLANK
    var deviceType: String? = KEY_ANDROID

    var clientName = ""
}