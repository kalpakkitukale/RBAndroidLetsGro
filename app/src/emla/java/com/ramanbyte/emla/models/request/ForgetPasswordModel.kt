package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR

class ForgetPasswordModel : BaseObservable() {
    @Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }
}