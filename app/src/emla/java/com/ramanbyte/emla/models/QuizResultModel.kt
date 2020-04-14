package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import com.ramanbyte.utilities.KEY_BLANK


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
class QuizResultModel() : Parcelable {

    var quizid = 0
    var obtainedmarks: Float = 0f
    var totalmarks: Float = 0f
    var passPercentage: Float = 0f
    var obtainedPercentage: Float = 0f
    var ispass: String = KEY_BLANK
    var passMessage: String? = KEY_BLANK
    var failMessage: String? = KEY_BLANK
    var attempted: Int = 0
    var correct: Int = 0
    var incorrect: Int = 0
    var attemptstatus: Int = 0


    constructor(parcel: Parcel) : this() {
        quizid = parcel.readInt()
        ispass = parcel.readString()!!
        passMessage = parcel.readString() ?: KEY_BLANK
        failMessage = parcel.readString() ?: KEY_BLANK
        attempted = parcel.readInt()
        correct = parcel.readInt()
        incorrect = parcel.readInt()
        attemptstatus = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(quizid)
        parcel.writeString(ispass)
        parcel.writeString(passMessage)
        parcel.writeString(failMessage)
        parcel.writeInt(attempted)
        parcel.writeInt(correct)
        parcel.writeInt(incorrect)
        parcel.writeInt(attemptstatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizResultModel> {
        override fun createFromParcel(parcel: Parcel): QuizResultModel {
            return QuizResultModel(parcel)
        }

        override fun newArray(size: Int): Array<QuizResultModel?> {
            return arrayOfNulls(size)
        }
    }

}