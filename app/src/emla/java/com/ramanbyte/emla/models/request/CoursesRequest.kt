package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseModel

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class CoursesRequest: BaseModel() {
    var userId: Int = 0
    var pageSize: Int = 10
    var searchKey: String = ""
}