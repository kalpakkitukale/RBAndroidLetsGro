package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseModel
import com.ramanbyte.emla.models.BaseRequestModel

class ChapterRequest : BaseRequestModel() {
    var courseId = 1
    var searchKey: String = ""
    var userId: Int = 0
}