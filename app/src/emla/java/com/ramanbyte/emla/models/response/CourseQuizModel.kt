package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
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
    var isAttempted: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.isAttempted)
        }
}