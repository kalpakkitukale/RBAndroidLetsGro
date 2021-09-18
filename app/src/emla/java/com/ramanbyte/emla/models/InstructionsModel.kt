package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import com.ramanbyte.utilities.KEY_BLANK


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class InstructionsModel() : Parcelable {
    var id: Int = 0
    var quiz_Title: String? = KEY_BLANK
    var total_Marks: Float? = 0.0f
    var quiz_Type: Int? = 0
    var common_Instruction: String? = KEY_BLANK
    var static_Instructions: String? = KEY_BLANK
    var total_Questions: String? = KEY_BLANK
    var highNumberOfQuestions: Int? = 0
    var middleNumberOfQuestions: Int? = 0
    var lowNumberOfQuestions: Int? = 0
    var passingPercent: Int? = 0
    var highNumberOfMarks: Int? = 0
    var middleNumberOfMarks: Int? = 0
    var lowNumberOfMarks: Int? = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        quiz_Title = parcel.readString()
        total_Marks = parcel.readFloat()
        quiz_Type = parcel.readInt()
        common_Instruction = parcel.readString()
        static_Instructions = parcel.readString()
        total_Questions = parcel.readString()
        highNumberOfQuestions = parcel.readInt()
        middleNumberOfQuestions = parcel.readInt()
        lowNumberOfQuestions = parcel.readInt()
        passingPercent = parcel.readInt()
        highNumberOfMarks = parcel.readInt()
        middleNumberOfMarks = parcel.readInt()
        lowNumberOfMarks = parcel.readInt()
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<InstructionsModel> {
        override fun createFromParcel(parcel: Parcel): InstructionsModel {
            return InstructionsModel(parcel)
        }

        override fun newArray(size: Int): Array<InstructionsModel?> {
            return arrayOfNulls(size)
        }
    }
}