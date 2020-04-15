package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR

class ChangePasswordModel : BaseObservable() {
    var userId: Int = 0

    @Bindable
    var oldPassword: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.oldPassword)
        }

    @Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @Bindable
    var newPassword: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.newPassword)
        }
}