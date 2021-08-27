package com.ramanbyte.emla.models.response

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_HYPHEN
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

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
    var jobSalaryRange: String? = KEY_BLANK
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
    var jobSkills: String? = KEY_BLANK
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

    @Bindable
    var isJobApplied: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.isJobApplied)
        }

    @Bindable
    var jobAppliedVisibility: Int? = View.GONE
        get() {
            return if (isJobApplied!! == 1) {
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

    @Bindable
    var vacancy: String? = KEY_BLANK

    @Bindable
    var experience: String? = KEY_BLANK

    @Bindable
    var companyDescription: String? = KEY_BLANK

    @Bindable
    var companyWebsite: String? = KEY_BLANK

    @Bindable
    var companyEmailId: String? = KEY_BLANK

}