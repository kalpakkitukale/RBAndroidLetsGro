package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseRequestModel

class JobRequestModel : BaseRequestModel() {

    var searchKey: String = ""
    var userId: Int = 0
    var SkillId: Int = 0
    var courseId = 1
}