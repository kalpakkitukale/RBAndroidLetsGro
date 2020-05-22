package com.ramanbyte.emla.faculty.models.request

import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_FACULTY

class FacultyCoursesRequestModel : BaseRequestModel() {
    var userId: Int = 0
    var userType: String = KEY_FACULTY
}