package com.ramanbyte.emla.models

import com.google.gson.annotations.SerializedName
/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

open class UserModel {

    var userId: Int = 0
    var user_Reff_Id: Int = 0
    var emailId = ""
    @SerializedName("isLoggedId")
    var loggedId = "N"

    @SerializedName("fName")
    var firstName = ""
    @SerializedName("mName")
    var middleName: String? = null
    @SerializedName("lName")
    var lastName: String? = null

    var userType = ""

    var manager: Boolean = false

    var classroomUserId: Int = 0

}