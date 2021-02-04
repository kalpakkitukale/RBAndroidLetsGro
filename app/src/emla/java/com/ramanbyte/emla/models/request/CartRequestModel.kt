package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.KEY_BLANK

class CartRequestModel  {
    var id: Int? = 0
    var userId: Int = 0
    var courseDetailsId: Int? = 0
    var createdBy: Int? = 0
    var createdDate: String? = KEY_BLANK
    var modifyBy: String? = KEY_BLANK
    var modifyDate: String? = KEY_BLANK

    var appStatus: String? = KEY_BLANK
    var delStatus: String? = KEY_BLANK
    var ipAddress: String? = KEY_BLANK

}