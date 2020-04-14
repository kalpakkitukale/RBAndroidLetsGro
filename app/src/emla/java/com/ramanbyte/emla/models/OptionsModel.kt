package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import com.ramanbyte.utilities.KEY_BLANK


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class OptionsModel() : Parcelable {

    var id: Int = 0
    var question_Id: Int = 0
    var options: String = KEY_BLANK
    var iscorrect: String = KEY_BLANK
    var createdDate: String = KEY_BLANK
    var modifiedDate: String = KEY_BLANK
    var app_Status: String = KEY_BLANK
    var del_Status: String = KEY_BLANK
    var isChecked = false
    var ipAddress: String = KEY_BLANK


 /*   var answer: Int? = 0
        set(value) {

            if (value != null)
                isChecked = value == id

            field = value
        }*/
    //var selectedOption : String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        question_Id = parcel.readInt()
        options = parcel.readString()!!
        iscorrect = parcel.readString()!!
        createdDate = parcel.readString()!!
        modifiedDate = parcel.readString()!!
        app_Status = parcel.readString()!!
        del_Status = parcel.readString()!!
        ipAddress = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(question_Id)
        parcel.writeString(options)
        parcel.writeString(iscorrect)
        parcel.writeString(createdDate)
        parcel.writeString(modifiedDate)
        parcel.writeString(app_Status)
        parcel.writeString(del_Status)
        parcel.writeString(ipAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OptionsModel> {
        override fun createFromParcel(parcel: Parcel): OptionsModel {
            return OptionsModel(parcel)
        }

        override fun newArray(size: Int): Array<OptionsModel?> {
            return arrayOfNulls(size)
        }
    }
}