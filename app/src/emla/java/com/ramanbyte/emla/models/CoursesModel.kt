package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class CoursesModel() : Parcelable {

    var courseId = 0
    var courseName: String? = null
    @SerializedName("description")
    var courseDescription: String? = null

    var courseCode: String? = null

    var courseImage: String? = null
    var courseImageUrl: String? = null
    var totalCount: Int = 0
    var startDateTime: String? = null
    var endDateTime: String? = null
    var preAssessmentStatus: String? = null
    var summativeAssessmentStatus: String? = null
    var summativeaAtemptCount: Int = 0


    constructor(parcel: Parcel) : this() {
        courseId = parcel.readInt()
        courseName = parcel.readString()
        courseDescription = parcel.readString()
        courseCode = parcel.readString()
        courseImage = parcel.readString()
        totalCount = parcel.readInt()
        startDateTime = parcel.readString() ?: ""
        endDateTime = parcel.readString() ?: ""
        preAssessmentStatus = parcel.readString()
        summativeAssessmentStatus = parcel.readString()
        summativeaAtemptCount = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(courseId)
        parcel.writeString(courseName)
        parcel.writeString(courseDescription)
        parcel.writeString(courseCode)
        parcel.writeString(courseImage)
        parcel.writeInt(totalCount)
        parcel.writeString(startDateTime)
        parcel.writeString(endDateTime)
        parcel.writeString(preAssessmentStatus)
        parcel.writeString(summativeAssessmentStatus)
        parcel.writeInt(summativeaAtemptCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CoursesModel> {
        override fun createFromParcel(parcel: Parcel): CoursesModel {
            return CoursesModel(parcel)
        }

        override fun newArray(size: Int): Array<CoursesModel?> {
            return arrayOfNulls(size)
        }
    }

}