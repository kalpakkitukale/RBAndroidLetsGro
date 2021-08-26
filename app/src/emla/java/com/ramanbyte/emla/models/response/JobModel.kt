package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

class JobModel : BaseObservable() {

    @SerializedName("id")
    var jobId: Int? = 0

    @SerializedName("title")
    @Bindable
    var jobTitle: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobTitle)
        }

    @SerializedName("title")
    @Bindable
    var companyName: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyName)
        }
}