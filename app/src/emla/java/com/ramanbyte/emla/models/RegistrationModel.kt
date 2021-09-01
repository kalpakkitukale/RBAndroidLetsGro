package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.KEY_DUMMY_DATE

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class RegistrationModel : BaseObservable() {
    var userDetails = UserDetailsModel()
    var areaofExperties = ArrayList<AreaOfExpertiesModel>()
}

class AreaOfExpertiesModel  {
    var id: Int? = 0
}

class UserDetailsModel : BaseObservable() {
    @Bindable
    var firstName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @Bindable
    var lastName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }

    @Bindable
    var password: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @Bindable
    var confirmPassword: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.confirmPassword)
        }

    @Bindable
    @SerializedName("email_Username")
    var emailUsername: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.emailUsername)
        }

    var user_username: String? = ""
    var user_anonymousname: String? = ""
    var countryName: String? = ""
    var app_Status: String? = KEY_APP
    var del_Status: String? = KEY_N
    var ipAddress: String? = IpUtility.getIpAddress()

    var address: String? = ""

    @Bindable
    var batchYearId: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.batchYearId)
        }

    var branchId: Int? = 0
    var branname: String? = ""
    var campusId: Int? = 0

    @Bindable
    var city: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    var clientId: Int? = 0

    @Bindable
    var contactNo: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.contactNo)
        }

    var countryId: Int? = 0
    var createdOn: String? = KEY_DUMMY_DATE
    var createdby: Int? = 0
    var currentAffiliation: String? = ""

    @Bindable
    var dateOfBirth: String? = KEY_DUMMY_DATE
        set(value) {
            field = value
            notifyPropertyChanged(BR.dateOfBirth)
        }

    @Bindable
    var dateofBirthstring: String? = ""
        set(value) {
            field = value

            if (value?.isNotEmpty() == true)
                dateOfBirth = DateUtils.getDisplayDateFromDate(
                    value!!,
                    DateUtils.DATE_DISPLAY_PATTERN_SEP,
                    DateUtils.DATE_WEB_API_FORMAT
                )

            notifyPropertyChanged(BR.dateofBirthstring)
        }
    /*get() {
        field = if(dateOfBirth.isNullOrBlank()) {
            return ""
        } else {
            DateUtils.getDisplayDateFromDate(
                dateOfBirth!!,
                DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
                DateUtils.DATE_DISPLAY_PATTERN_SEP
            )
        }
        return field
    }*/

    var department: Int? = 0
    var deptname: String? = ""
    var designame: String? = ""
    var designationId: Int? = 0
    var employeeFullName: String? = ""
    var employeeId: String? = ""
    var gender: String? = ""
    var id: Int? = 0

    @Bindable
    var instituteName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.instituteName)
        }


    var isActive: String? = ""

    @Bindable
    var middleName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.middleName)
        }


    var mobileContryCode: String? = ""
    var modify_By: Int? = 0
    var modify_Date: String? = KEY_DUMMY_DATE

    @Bindable
    var patternId: Int? = -1
        set(value) {
            field = value
            notifyPropertyChanged(BR.patternId)
        }

    var programId: Int? = 0

    @Bindable
    var programLevelId: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.programLevelId)
        }

    var reporting_Head: Int? = 0

    @Bindable
    var specializationId: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.specializationId)
        }

    @Bindable
    var state: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    @Bindable
    var universityName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.universityName)
        }

    var userImageFilename: String? = ""
    var userImageString: String? = ""
    var userTitle: String? = ""
    var user_Reff_Id: Int? = 0
    var user_Role: Int? = 0
    var user_Status: String? = ""
    var userType: String? = KEY_STUDENT
    var yearofExperience: Int? = 0

    @Bindable
    var cityName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cityName)
        }

    var stateName = ""
    var educationLevlName = ""
    @Bindable
    var pattern = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.pattern)
        }

    var specializationName = ""
    var resumeFileName: String? = ""
    var linkedInProfile = ""
    var message = ""

    var user_userDelStatus = KEY_N
    var user_userStatus = KEY_APP
    var user_userIsActive = KEY_Y
    var classroomUserId: Int = 0
    var jid: String = ""
}

