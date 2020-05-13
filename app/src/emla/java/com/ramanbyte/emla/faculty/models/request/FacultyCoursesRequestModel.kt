package com.ramanbyte.emla.faculty.models.request

import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.KEY_BLANK

class FacultyCoursesRequestModel : BaseRequestModel() {
    var userId: Int = 0
    var userType: String = KEY_BLANK
}