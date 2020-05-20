package com.ramanbyte.emla.models

import android.graphics.drawable.Drawable
import androidx.room.Ignore
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.ViewUtils

class AskQuestionReplyModel {
    var userId = 0

    /*
    * this is for question list
    * */
    var createdDateTime: String? = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field!!,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_TIME_PATTERN
        )

    /*
    * this is for reply list
    * */
    var createDateTime: String? = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field!!,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_TIME_PATTERN
        )

    var answer = KEY_BLANK
    var userName = KEY_BLANK
    var userType = KEY_BLANK
    var userPic: String? = KEY_BLANK
        get() = field ?: KEY_BLANK

    @Ignore
    var setCharacterDrawable: Drawable? = null
        get() {
            field = ViewUtils.getCharacterDrawable(userName.substring(0, 1))
            return field
        }
}