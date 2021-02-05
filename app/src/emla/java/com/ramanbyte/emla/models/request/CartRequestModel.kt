package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.*

class CartRequestModel {
    var id: Int? = 0
    var userId: Int = 0
    var courseDetailsId: Int? = 0
    var createdBy: Int? = 0
    var createdDate: String? =
        DateUtils.getCurrentDateTime(DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var modifyBy: Int? = 0
    var modifyDate: String? =
        DateUtils.getCurrentDateTime(DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var appStatus: String? = APP_STATUS
    var delStatus: Boolean = DEL_STATUS1
    var ipAddress: String? = IpUtility.getIpAddress()

}