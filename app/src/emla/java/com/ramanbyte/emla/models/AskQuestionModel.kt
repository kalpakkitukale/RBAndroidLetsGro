package com.ramanbyte.emla.models

import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.DateUtils.DATE_TIME_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import com.ramanbyte.utilities.KEY_BLANK

class AskQuestionModel {

    var id = 0
    var question = KEY_BLANK
    var created_Date = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DATE_TIME_PATTERN
        )

    var isPublic = KEY_BLANK

}