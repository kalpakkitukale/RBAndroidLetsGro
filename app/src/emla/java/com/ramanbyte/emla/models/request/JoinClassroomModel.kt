package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_STUDENT

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 11/2/21
 */
class JoinClassroomModel : BaseObservable() {

    @Bindable
    var username: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.username)
        }

    @Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    var userId: Int = 0

    var userType : String = ""
}