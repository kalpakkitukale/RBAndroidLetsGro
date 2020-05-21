package com.ramanbyte.emla.faculty.models.request

import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_N
import com.ramanbyte.utilities.KEY_Y

class StudentAskedQuestionsRequestModel : BaseRequestModel(){
    var userId: Int = 0
    var courseId: Int = 0
    var searchString: Int = 0
    var isQuestionAnswered: String = KEY_N
    var dateWiseSort: String = KEY_BLANK
}