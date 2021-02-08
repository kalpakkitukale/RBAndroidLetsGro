package com.ramanbyte.emla.models.response

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.ramanbyte.utilities.KEY_BLANK

class CartResponseModel() : Parcelable, BaseObservable() {
    var courseName: String? = KEY_BLANK
    var courseDescription: String? = KEY_BLANK
    var duraton: String? = KEY_BLANK
    var courseFee: String? = KEY_BLANK
    var courseDetailsId: Int? = 0
    var courseImageUrl: String? = null
    var courseImage: String? = null
    var id: Int? = 0
    var modifyBy: String? = KEY_BLANK
    var modifyDate: String? = KEY_BLANK
    var createdDate: String? = KEY_BLANK
    var appStatus: String? = KEY_BLANK
    var delStatus: String? = KEY_BLANK

    constructor(parcel: Parcel) : this() {
        courseName = parcel.readString()
        courseDescription = parcel.readString()
        duraton = parcel.readString()
        courseFee = parcel.readString()
        courseDetailsId = parcel.readValue(Int::class.java.classLoader) as? Int
        courseImageUrl = parcel.readString()
        courseImage = parcel.readString()
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        modifyBy = parcel.readString()
        modifyDate = parcel.readString()
        createdDate = parcel.readString()
        appStatus = parcel.readString()
        delStatus = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseName)
        parcel.writeString(courseDescription)
        parcel.writeString(duraton)
        parcel.writeString(courseFee)
        parcel.writeValue(courseDetailsId)
        parcel.writeString(courseImageUrl)
        parcel.writeString(courseImage)
        parcel.writeValue(id)
        parcel.writeString(modifyBy)
        parcel.writeString(modifyDate)
        parcel.writeString(createdDate)
        parcel.writeString(appStatus)
        parcel.writeString(delStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartResponseModel> {
        override fun createFromParcel(parcel: Parcel): CartResponseModel {
            return CartResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<CartResponseModel?> {
            return arrayOfNulls(size)
        }
    }


}