package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.KEY_DOMAIN

class JobRequestModel : BaseRequestModel() {

    var searchkey: String = ""
    var userId: Int = 0
    var skillId: Int = 0
    var domain = KEY_DOMAIN
    var jobId: Int = 0
}