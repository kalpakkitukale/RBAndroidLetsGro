package com.ramanbyte.emla.faculty.models

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Ignore
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_NA_WITHOUT_SPACE
import com.ramanbyte.utilities.ViewUtils

class StudentAskedQuestionsModel() : Parcelable {
    var studentId: Int = 0
    var courseName: String? = KEY_BLANK
    var chapterName: String? = KEY_BLANK
    var sectionName: String? = KEY_BLANK
    var courseId: Int = 0
    var chapterId: Int = 0
    var sectionId: Int = 0
    var studentName: String? = KEY_BLANK
    var studentPic: String? = KEY_BLANK
    var studentPhoneNo: String? = KEY_BLANK
    var question: String? = KEY_BLANK
        get() = field ?: KEY_NA_WITHOUT_SPACE
    var videoId: Int = 0
    var studentEmail: String? = KEY_BLANK
    var questionId: Int = 0
    var questionRaisedDateTime: String = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_TIME_PATTERN
        )

    @Ignore
    var setCharacterDrawable: Drawable? = null
        get() {
            field = ViewUtils.getCharacterDrawable(studentName?.substring(0, 1)!!)
            return field
        }

    constructor(parcel: Parcel) : this() {
        studentId = parcel.readInt()
        courseName = parcel.readString()
        chapterName = parcel.readString()
        sectionName = parcel.readString()
        courseId = parcel.readInt()
        chapterId = parcel.readInt()
        sectionId = parcel.readInt()
        studentName = parcel.readString()
        studentPic = parcel.readString()
        studentPhoneNo = parcel.readString()
        videoId = parcel.readInt()
        studentEmail = parcel.readString()
        questionId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(studentId)
        parcel.writeString(courseName)
        parcel.writeString(chapterName)
        parcel.writeString(sectionName)
        parcel.writeInt(courseId)
        parcel.writeInt(chapterId)
        parcel.writeInt(sectionId)
        parcel.writeString(studentName)
        parcel.writeString(studentPic)
        parcel.writeString(studentPhoneNo)
        parcel.writeInt(videoId)
        parcel.writeString(studentEmail)
        parcel.writeInt(questionId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudentAskedQuestionsModel> {
        override fun createFromParcel(parcel: Parcel): StudentAskedQuestionsModel {
            return StudentAskedQuestionsModel(parcel)
        }

        override fun newArray(size: Int): Array<StudentAskedQuestionsModel?> {
            return arrayOfNulls(size)
        }
    }
}