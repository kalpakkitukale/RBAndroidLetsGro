package com.ramanbyte.emla.models.request

import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.KEY_APP
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_N

class QuestionsReplyRequestModel {
    var id: Int? = 0
    var question_Id: Int? = 0
    var userId: Int? = 0
    var answer: String? = KEY_BLANK
    var created_Date: String? = DateUtils.getCurDate()
    var app_Status: String? = KEY_APP
    var del_Status: String? = KEY_N
    var isFavourite: String? = KEY_BLANK
}