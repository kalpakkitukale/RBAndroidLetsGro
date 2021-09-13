package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR

class CourseQuizResultModel : BaseObservable() {

    @Bindable
    var totalQuestion: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalQuestion)
        }

    @Bindable
    var attemptedQuestion: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.attemptedQuestion)
        }

    @Bindable
    var correctQuestion: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.correctQuestion)
        }

    @Bindable
    var isPass: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.isPass)
        }
}