package com.ramanbyte.emla.models

import com.google.gson.annotations.SerializedName
/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

open class UserModel {

    var userId: Int = 0
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

//    var username   : String = ""       ": "vinay k",
//    var userId     : String = ""   ": 2,
//    var emailId    : String = ""   ": "vinay.k@ramanbyte.com",
//    var isLoggedId : String = ""       ": "1",
//    var fName      : String = ""   ": "vinay",
//    var lName      : String = ""   ": "k",
//    var mName      : String = ""   ": null,
//    var userType   : String = ""       ": "STF",
//    var isActive   : String = ""       ": "Y"
}