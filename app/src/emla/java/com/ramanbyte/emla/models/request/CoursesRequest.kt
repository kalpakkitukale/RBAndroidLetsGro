package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseRequestModel

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class CoursesRequest: BaseRequestModel() {
    var userId: Int = 0
    var searchKey: String = ""

    var userType: String = ""
    var programId: Int = 0
    var specializationId: Int = 0
    var patternId: Int = 0
    var skillId: Int = 0
}