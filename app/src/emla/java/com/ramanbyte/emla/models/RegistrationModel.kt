package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.IpUtility

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class RegistrationModel : BaseObservable() {
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

    var address: String? = ""
    var batchYearId: Int? = 0
    var branchId: Int? = 0
    var branname: String? = ""
    var campusId: Int? = 0
    var city: Int? = 0
    var clientId: Int? = 0
    var contactNo: String? = ""
    var countryId: Int? = 0
    var createdOn: String? = ""
    var createdby: Int? = 0
    var currentAffiliation: String? = ""
    var dateOfBirth: String? = ""
    var dateofBirthstring: String? = ""
    var department: Int? = 0
    var deptname: String? = ""
    var designame: String? = ""
    var designationId: Int? = 0
    var employeeFullName: String? = ""
    var employeeId: String? = ""
    var gender: String? = ""
    var id: Int? = 0
    var instituteName: String? = ""
    var isActive: String? = ""
    var middleName: String? = ""
    var mobileContryCode: String? = ""
    var modify_By: Int? = 0
    var modify_Date: String? = ""
    var patternId: Int? = 0
    var programId: Int? = 0
    var programLevelId: Int? = 0
    var reporting_Head: Int? = 0
    var specializationId: Int? = 0
    var state: Int? = 0
    var universityName: String? = ""
    var userImageFilename: String? = ""
    var userImageString: String? = ""
    var userTitle: String? = ""
    var user_Reff_Id: Int? = 0
    var user_Role: Int? = 0
    var user_Status: String? = ""
    var yearofExperience: Int? = 0
}