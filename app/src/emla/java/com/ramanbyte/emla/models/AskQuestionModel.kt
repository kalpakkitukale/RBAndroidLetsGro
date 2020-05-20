package com.ramanbyte.emla.models

import android.graphics.drawable.Drawable
import androidx.room.Ignore
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_TIME_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

class AskQuestionModel {

    var questionId = 0
    var totalReplyCount = 0
    var question = KEY_BLANK
        get() = field ?: KEY_NA_WITHOUT_SPACE
    var userName = KEY_BLANK
    var userPic: String? = KEY_BLANK
        get() = field ?: KEY_BLANK

    var createdDateTime = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DATE_TIME_PATTERN
        )

    var isPublic = KEY_BLANK
    var isApproved = KEY_BLANK
    var isConversionOpen = KEY_BLANK
    var qnaArrayList = ArrayList<AskQuestionReplyModel>()

    @Ignore
    var setCharacterDrawable: Drawable? = null
        get() {
            field = ViewUtils.getCharacterDrawable(userName.substring(0, 1))
            return field
        }

}
