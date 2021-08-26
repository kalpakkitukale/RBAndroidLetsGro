package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

class SkillsModel : BaseObservable() {

    @SerializedName("chapterId")
    var skillId: Int? = 0

    @SerializedName("chapterName")
    @Bindable
    var skillName: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.skillName)
        }

    @SerializedName("totalSectionCount")
    @Bindable
    var totalNumberOfJobs: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalNumberOfJobs)
        }
}