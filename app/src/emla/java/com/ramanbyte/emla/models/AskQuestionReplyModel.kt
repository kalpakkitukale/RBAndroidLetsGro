package com.ramanbyte.emla.models

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Ignore
import com.ramanbyte.R
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.getFreeFormatDateTime

class AskQuestionReplyModel() : Parcelable {
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

    var answer: String? = KEY_BLANK
    var userName: String? = KEY_BLANK
    var userType: String? = KEY_BLANK
    var userPic: String? = KEY_BLANK
        get() = field ?: KEY_BLANK

    @Ignore
    var setCharacterDrawable: Drawable? = null
        get() {
            field = if (userType == KEY_FACULTY)
                userName?.substring(0, 1)?.let { ViewUtils.getCharacterDrawable(it) }
            else
                ViewUtils.getCharacterDrawable(BindingUtils.string(R.string.self).substring(0, 1))
            return field
        }// ViewUtils.getCharacterDrawable(userName?.substring(0, 1))

    @Ignore
    var setCharacterDrawableForFaculty: Drawable? = null
        get() {
            field = userName?.substring(0, 1)?.let { ViewUtils.getCharacterDrawable(it) }
            return field
        }

    constructor(parcel: Parcel) : this() {
        userId = parcel.readInt()
        answer = parcel.readString()
        userName = parcel.readString()
        userType = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(answer)
        parcel.writeString(userName)
        parcel.writeString(userType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AskQuestionReplyModel> {
        override fun createFromParcel(parcel: Parcel): AskQuestionReplyModel {
            return AskQuestionReplyModel(parcel)
        }

        override fun newArray(size: Int): Array<AskQuestionReplyModel?> {
            return arrayOfNulls(size)
        }
    }
}