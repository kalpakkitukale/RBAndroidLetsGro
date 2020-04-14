package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseModel
import com.ramanbyte.emla.models.BaseRequestModel

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class CoursesRequest: BaseRequestModel() {
    var userId: Int = 0
    var searchKey: String = ""
}