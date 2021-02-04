package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import com.ramanbyte.utilities.KEY_BLANK

class CartResponseModel() : BaseObservable(){
    var courseName: String? = KEY_BLANK
    var courseDescription: String? = KEY_BLANK
    var duraton: String? = KEY_BLANK
    var courseFees: String? = KEY_BLANK
    var courseDetailsId: Int? = 0
    var id: Int? = 0
    var modifyBy: String? = KEY_BLANK
    var modifyDate: String? = KEY_BLANK
    var createdDate: String? = KEY_BLANK
    var appStatus: String? = KEY_BLANK
    var delStatus: String? = KEY_BLANK


}