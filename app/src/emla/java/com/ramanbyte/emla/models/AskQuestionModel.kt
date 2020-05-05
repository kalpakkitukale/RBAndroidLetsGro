package com.ramanbyte.emla.models

import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.DateUtils.DATE_TIME_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import com.ramanbyte.utilities.KEY_BLANK

class AskQuestionModel {

    var questionId = 0
    var totalReplyCount = 0
    var question = KEY_BLANK
    var userName = KEY_BLANK
    var userPic = KEY_BLANK
    var createdDateTime = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DATE_TIME_PATTERN
        )

    var isPublic = KEY_BLANK
    var isApproved = KEY_BLANK
    var qnaArrayList = ArrayList<ReplyModel>()


}

class ReplyModel{
    var userId = 0
    var createdDateTime = KEY_BLANK
    var answer = KEY_BLANK
    var userName = KEY_BLANK
    var userPic = KEY_BLANK
}