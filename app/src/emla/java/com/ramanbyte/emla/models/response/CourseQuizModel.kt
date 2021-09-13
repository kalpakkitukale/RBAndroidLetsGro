package com.ramanbyte.emla.models.response

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_HYPHEN

class CourseQuizModel : BaseObservable() {

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
}