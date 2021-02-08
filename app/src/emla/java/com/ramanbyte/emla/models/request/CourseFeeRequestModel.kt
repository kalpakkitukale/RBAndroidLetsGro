package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable
import com.ramanbyte.utilities.*

/**
 * @author Mansi Manakiki Mody
 * @since 5 Feb 2021
 * @description Model class for course fee for transaction
 */
class CourseFeeRequestModel : BaseObservable() {

    var id: Int = 0
    var userId: Int = 0
    var paymentId: Int = 0
    var courseDetailsId: Int = 0
    var courseFeeStructureId: Int = 0
    var createdDate: String? = DateUtils.getCurDate(
        DATE_TIME_SECONDS_PATTERN
    )
    var appStatus: String = APP_STATUS
    var delStatus: Boolean = DEL_STATUS1
    var ipAddress: String = IpUtility.getIpAddress()
    var deviceId: Int = 0
    var deviceType: String? = KEY_ANDROID


}