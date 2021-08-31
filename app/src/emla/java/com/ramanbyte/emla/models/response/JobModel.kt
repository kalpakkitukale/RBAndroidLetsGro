package com.ramanbyte.emla.models.response

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.*

class JobModel : BaseObservable() {

    @SerializedName("id")
    var jobId: Int? = 0

    /*Role*/
    @SerializedName("title")
    @Bindable
    var jobTitle: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobTitle)
        }

    @Bindable
    var companyName: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyName)
        }


    @SerializedName("locations")
    @Bindable
    var jobLocation: String? = KEY_BLANK
        //"Nodia, Pune, Mumbai, Delhi, Kolkata"
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobLocation)
        }

    @Bindable
    var minSalaryOffered: Long? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.minSalaryOffered)
        }

    @Bindable
    var maxSalaryOffered: Long? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.maxSalaryOffered)
        }

    @Bindable
    var jobSalaryRange: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobSalaryRange)
        }
        get() {
            var minSalary = StaticMethodUtilitiesKtx.convertAmountToDisplay(minSalaryOffered!!)
            var maxSalary = StaticMethodUtilitiesKtx.convertAmountToDisplay(maxSalaryOffered!!)
            return "$minSalary - $maxSalary"
        }

    @SerializedName("skills")
    @Bindable
    var jobSkills: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobSkills)
        }

    @SerializedName("postedOn")
    @Bindable
    var jobPostedOn: String? = KEY_HYPHEN
        set(value) {
            field = value
            notifyPropertyChanged(BR.jobPostedOn)
        }
        get() = DateUtils.getDisplayDateFromDate(
            field!!,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_DISPLAY_PATTERN
        )

    @SerializedName("isApplied")
    @Bindable
    var isJobApplied: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.isJobApplied)
        }

    @Bindable
    var jobAppliedVisibility: Int? = View.GONE
        get() {
            AppLog.infoLog("Model Job Applied ----- $isJobApplied")
            return if (isJobApplied!! == 1) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    @Bindable
    var companyLogo: String? = KEY_BLANK
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyLogo)
        }

    @Bindable
    var companyImageURL: String? = KEY_BLANK

    @SerializedName("noofPositions")
    @Bindable
    var vacancy: Int? = 0

    @Bindable
    var experience: String? = KEY_HYPHEN

    @Bindable
    var companyDescription: String? = KEY_HYPHEN

    @Bindable
    var companyWebsite: String? = KEY_HYPHEN

    @Bindable
    var companyEmailId: String? = KEY_HYPHEN

}