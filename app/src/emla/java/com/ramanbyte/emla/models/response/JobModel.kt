package com.ramanbyte.emla.models.response

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.DateUtils
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

    @Bindable
    var jobPostedOn: String? = "2021-08-27T17:35:58.620Z"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobPostedOn)
        }
        get() = DateUtils.getDisplayDateFromDate(
            field!!,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_DISPLAY_PATTERN
        )

    @Bindable
    var isJobApplied: Boolean? = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.isJobApplied)
        }

    @Bindable
    var jobAppliedVisibility: Int? = View.GONE
        get() {
            return if (isJobApplied!!) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    @Bindable
    var companyLogo: String? = "Apr2020/download (1)_5_6_920200514140000.jpg"
        //KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyLogo)
        }

    @Bindable
    var companyImageURL: String? = KEY_BLANK

}