package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.KEY_BLANK


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 16/3/20
 */
class ManageUserDeviceModel {

    var deviceId: Int = 0
    var fcmToken: String = KEY_BLANK
    var deviceName: String = KEY_BLANK
    var deviceModel: String = KEY_BLANK
    var deviceOS: String = KEY_BLANK
    var imei: String = KEY_BLANK
    var lastUsedDateTime: String = KEY_BLANK
    var activeStatus: Int = 0
    var userId: Int = 0
    var appVersion: String = KEY_BLANK
    var modifiedDateTime: String = KEY_BLANK
    var modifiedByUserId: Int = 0
    var deviceIP: String = KEY_BLANK
    var createdDateTime: String = KEY_BLANK
    var logFrom: String = KEY_BLANK
    var logStatus: String = KEY_BLANK

}