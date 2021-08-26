package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

class JobModel : BaseObservable() {

    @SerializedName("chapterId")
    var jobId: Int? = 0

    @SerializedName("chapterName")
    @Bindable
    var jobTitle: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobTitle)
        }

    @SerializedName("description")
    @Bindable
    var companyName: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyName)
        }


    @Bindable
    var jobLocation: String? = "Nodia, Pune, Mumbai, Delhi, Kolkata"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobLocation)
        }

    @Bindable
    var jobSalaryRange: String? = "8K - 16K"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobSalaryRange)
        }

    @Bindable
    var jobSkills: String? =
        "Interface Design, Adobe XD, Figma, Prototype, Typography, Visual Design, Color Therapy"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobSkills)
        }
}