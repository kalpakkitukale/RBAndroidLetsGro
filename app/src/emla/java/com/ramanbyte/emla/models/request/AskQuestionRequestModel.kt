package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.*

class AskQuestionRequestModel {

    var id: Int? = 0
    var student_Id: Int? = 0
    var course_Id: Int? = 0
    var chpater_Id: Int? = 0
    var section_Id: Int? = 0
    var content_Id: Int? = 0
    var faculty_Id: Int? = 0
    var content_Type: String? = KEY_BLANK
    var device_Id: String? = KEY_BLANK
    var question: String? = KEY_BLANK
    var answer: String? = KEY_BLANK
    var created_Date: String? = DateUtils.getCurDate()
    var app_status: String? = KEY_APP
    var del_Status: String? = KEY_N
    var isPublic: String? = KEY_N
    var isApproved: String? = KEY_N
    var isConversationOpen: String? = KEY_Y

}