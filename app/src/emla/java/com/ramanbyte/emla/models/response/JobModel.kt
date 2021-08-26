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
}