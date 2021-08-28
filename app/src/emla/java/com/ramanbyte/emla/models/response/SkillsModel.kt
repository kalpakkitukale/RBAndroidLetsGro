package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

class SkillsModel : BaseObservable() {

    @SerializedName("id")
    var skillId: Int? = 0

    @SerializedName("title")
    @Bindable
    var skillName: String? = KEY_BLANK
        //"Website Development"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.skillName)
        }

    @SerializedName("noofJobs")
    @Bindable
    var totalNumberOfJobs: Int? = 0
        //150
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalNumberOfJobs)
        }
}