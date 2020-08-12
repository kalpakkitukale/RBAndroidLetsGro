package com.ramanbyte.emla.models

import android.graphics.drawable.Drawable
import androidx.room.Ignore
import com.ramanbyte.R
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.getFreeFormatDateTime

class AskQuestionReplyModel {
    var userId = 0

    /*
    * this is for question list
    * */
    var createdDateTime: String? = KEY_BLANK
        get() = getFreeFormatDateTime(
            DateUtils.getTimeFormDate(
                field!!,
                DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
            ),
            DateUtils.getCalendarByCustomDate(
                field!!,
                DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
            )!!
        )

    var answer = KEY_BLANK
    var userName = KEY_BLANK
    var userType = KEY_BLANK
    var userPic: String? = KEY_BLANK
        get() = field ?: KEY_BLANK

    @Ignore
    var setCharacterDrawable: Drawable? = null
        get() {
            field = if (userType == KEY_FACULTY)
                ViewUtils.getCharacterDrawable(userName.substring(0, 1))
            else
                ViewUtils.getCharacterDrawable(BindingUtils.string(R.string.self).substring(0, 1))
            return field
        }

    @Ignore
    var setCharacterDrawableForFaculty: Drawable? = null
        get() {
            field = ViewUtils.getCharacterDrawable(userName.substring(0, 1))
            return field
        }
}