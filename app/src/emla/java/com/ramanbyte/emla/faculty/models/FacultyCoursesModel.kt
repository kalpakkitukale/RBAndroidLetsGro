package com.ramanbyte.emla.faculty.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.utilities.KEY_BLANK

class FacultyCoursesModel() : Parcelable{
    var courseId = 0
    var courseName: String? = KEY_BLANK
    var courseDescription: String? = KEY_BLANK
    var courseCode: String? = KEY_BLANK
    var courseImage: String? = KEY_BLANK
    var courseImageUrl: String? = KEY_BLANK
    var startDateTime: String? = KEY_BLANK
    var endDateTime: String? = KEY_BLANK
    var totalNumberOfQuestionCount: Int? = 0

    constructor(parcel: Parcel) : this() {
        courseId = parcel.readInt()
        courseName = parcel.readString()
        courseDescription = parcel.readString()
        courseCode = parcel.readString()
        courseImage = parcel.readString()
        courseImageUrl = parcel.readString()
        startDateTime = parcel.readString()
        endDateTime = parcel.readString()
        totalNumberOfQuestionCount = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(courseId)
        parcel.writeString(courseName)
        parcel.writeString(courseDescription)
        parcel.writeString(courseCode)
        parcel.writeString(courseImage)
        parcel.writeString(courseImageUrl)
        parcel.writeString(startDateTime)
        parcel.writeString(endDateTime)
        parcel.writeValue(totalNumberOfQuestionCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FacultyCoursesModel> {
        override fun createFromParcel(parcel: Parcel): FacultyCoursesModel {
            return FacultyCoursesModel(parcel)
        }

        override fun newArray(size: Int): Array<FacultyCoursesModel?> {
            return arrayOfNulls(size)
        }
    }
}