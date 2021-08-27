package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR

class SkillsModel : BaseObservable() {

    @SerializedName("chapterId")
    var skillId: Int? = 0

    @Bindable
    var skillName: String? = "Website Development"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.skillName)
        }

    @Bindable
    var totalNumberOfJobs: Int? = 150
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalNumberOfJobs)
        }
}