package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseModel
import com.ramanbyte.emla.models.BaseRequestModel

class SectionsRequest : BaseRequestModel() {

    var searchKey: String? = ""
    var chapterId: Int? = 0
}