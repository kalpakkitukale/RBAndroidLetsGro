package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.utilities.KEY_BLANK


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class QuestionAndAnswerModel() : Parcelable {

    var id: Int = 0
    var program_Id: Int = 0
    var department_Id: Int = 0
    var course_Id: Int = 0
    var topic_Id: Int = 0
    var question_Type: Int = 0
    var difficulty_Level: Int = 0
    @SerializedName("isPublic")
    var publicStatus: String? = KEY_BLANK
    var question_Title: String = KEY_BLANK
        set(value) {

            var tempVal = value

            /*if (tempVal.contains("table", true)) {


                val indexOfTable = tempVal.indexOf("<t")

                val firstIndexOfAngle = tempVal.indexOfFirst { it == '>' }


                tempVal =
                    tempVal.replace(tempVal.substring(indexOfTable, firstIndexOfAngle + 1), "")

                val indexOfTabClose = tempVal.lastIndexOf("</table>")

                tempVal = tempVal.replace(tempVal.substring(indexOfTabClose), "")
            }*/


            field = tempVal

        }
    var marks: Double = 0.0
    var createdDate: String = KEY_BLANK
    var modifiedDate: String = KEY_BLANK
    var app_Status: String = KEY_BLANK
    var del_Status: String = KEY_BLANK
    var ipAddress: String = KEY_BLANK
    var total_Options: Int = 0
    @SerializedName("isarchive")
    var archive: String = KEY_BLANK
    var quizid: Int = 0
    var passignpercentage: Double = 0.0

    var questionDetails = ArrayList<OptionsModel>()

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        program_Id = parcel.readInt()
        department_Id = parcel.readInt()
        course_Id = parcel.readInt()
        topic_Id = parcel.readInt()
        question_Type = parcel.readInt()
        difficulty_Level = parcel.readInt()
        publicStatus = parcel.readString()
        question_Title = parcel.readString()!!
        marks = parcel.readDouble()
        createdDate = parcel.readString()!!
        modifiedDate = parcel.readString()!!
        app_Status = parcel.readString()!!
        del_Status = parcel.readString()!!
        ipAddress = parcel.readString()!!
        total_Options = parcel.readInt()
        archive = parcel.readString()!!
        quizid = parcel.readInt()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(program_Id)
        parcel.writeInt(department_Id)
        parcel.writeInt(course_Id)
        parcel.writeInt(topic_Id)
        parcel.writeInt(question_Type)
        parcel.writeInt(difficulty_Level)
        parcel.writeString(publicStatus)
        parcel.writeString(question_Title)
        parcel.writeDouble(marks)
        parcel.writeString(createdDate)
        parcel.writeString(modifiedDate)
        parcel.writeString(app_Status)
        parcel.writeString(del_Status)
        parcel.writeString(ipAddress)
        parcel.writeInt(total_Options)
        parcel.writeString(archive)
        parcel.writeInt(quizid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionAndAnswerModel> {
        override fun createFromParcel(parcel: Parcel): QuestionAndAnswerModel {
            return QuestionAndAnswerModel(parcel)
        }

        override fun newArray(size: Int): Array<QuestionAndAnswerModel?> {
            return arrayOfNulls(size)
        }
    }

    var clearOptions = {}
}

