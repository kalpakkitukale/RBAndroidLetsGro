package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginRequest : BaseObservable() {

    @SerializedName("username")
    @Bindable
    var emailId: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.emailId)
        }

    @Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    var isFromCPPlus: String = "N"
}