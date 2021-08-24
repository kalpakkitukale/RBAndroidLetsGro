package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

class SkillsModel() : BaseObservable() {

    var skillId: Int? = 0

    @Bindable
    var skillName: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.skillName)
        }

    @Bindable
    var totalNumberOfJobs: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalNumberOfJobs)
        }
}