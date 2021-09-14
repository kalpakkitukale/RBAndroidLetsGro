package com.ramanbyte.emla.models.response

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_HYPHEN

class CourseQuizModel() : BaseObservable(), Parcelable {

    var quizId: Int? = 0

    @Bindable
    var quizTitle: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizTitle)
        }


    @Bindable
    var quizDescription: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizDescription)
        }

    @Bindable
    var quizStartDateTime: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizStartDateTime)
        }

    @Bindable
    var quizEndDateTime: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizEndDateTime)
        }

    @Bindable
    var quizTotalMarks: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizTotalMarks)
        }

    @Bindable
    var isAttempted: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.isAttempted)
        }

    @Bindable
    var resultButtonVisibility: Int? = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.resultButtonVisibility)
        }
        get() {
            AppLog.infoLog("Quiz Attempted ----- $isAttempted")
            return if (isAttempted!! == 1) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    @Bindable
    var quizButtonVisibility: Int? = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.quizButtonVisibility)
        }
        get() {
            AppLog.infoLog("Quiz Attempted ----- $isAttempted")
            return if (isAttempted!! == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    constructor(parcel: Parcel) : this() {
        quizId = parcel.readValue(Int::class.java.classLoader) as? Int
        quizTitle = parcel.readValue(String::class.java.classLoader) as? String
        quizDescription = parcel.readValue(String::class.java.classLoader) as? String
        quizStartDateTime = parcel.readValue(String::class.java.classLoader) as? String
        quizEndDateTime = parcel.readValue(String::class.java.classLoader) as? String
        quizTotalMarks = parcel.readValue(String::class.java.classLoader) as? String
        isAttempted = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(quizId)
        parcel.writeValue(quizTitle)
        parcel.writeValue(quizDescription)
        parcel.writeValue(quizStartDateTime)
        parcel.writeValue(quizEndDateTime)
        parcel.writeValue(quizTotalMarks)
        parcel.writeValue(isAttempted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CourseQuizModel> {
        override fun createFromParcel(parcel: Parcel): CourseQuizModel {
            return CourseQuizModel(parcel)
        }

        override fun newArray(size: Int): Array<CourseQuizModel?> {
            return arrayOfNulls(size)
        }
    }
}