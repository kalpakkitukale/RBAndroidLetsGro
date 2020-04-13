package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable
import com.ramanbyte.utilities.APP_STATUS
import com.ramanbyte.utilities.DEL_STATUS
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.IpUtility

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 11/3/20
 */
abstract class BaseModel : BaseObservable() {
    var createdBy: Int? = 0
    var modifiedBy: Int? = 0
    var ipAddress: String? = IpUtility.getIpAddress()
    var ipaddress: String? = IpUtility.getIpAddress()
    var createdDate: String? =
        DateUtils.getCurrentDateTime(DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var modifiedDate: String? =
        DateUtils.getCurrentDateTime(DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var app_Status: String? = APP_STATUS
    var appStatus: String? = APP_STATUS
    var del_Status: String? = DEL_STATUS
    var delStatus: String? = DEL_STATUS
}