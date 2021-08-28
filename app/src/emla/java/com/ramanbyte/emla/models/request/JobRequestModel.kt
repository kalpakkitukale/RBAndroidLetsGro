package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.IpUtility
import com.ramanbyte.utilities.KEY_DOMAIN

open class JobRequestModel : BaseRequestModel() {

    var searchkey: String = ""
    var userId: Int = 0
    var skillId: Int = 0
    var domain = KEY_DOMAIN
    var jobId: Int = 0

}

class ApplyJobRequestModel(userId: Int, jobId: Int) : JobRequestModel() {
    init {
        this.userId = userId
        this.jobId = jobId
    }

    var ipAddress: String = IpUtility.getIpAddress()

}