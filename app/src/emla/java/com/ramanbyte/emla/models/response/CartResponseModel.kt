package com.ramanbyte.emla.models.response

import androidx.databinding.BaseObservable
import com.ramanbyte.utilities.KEY_BLANK

class CartResponseModel() : BaseObservable(){
    var channelPartnerName: String? = KEY_BLANK
    var sessionTitle: String? = KEY_BLANK
    var channelPartnerId: Int? = 0
    var channelPartnerContactId: Int? = 0
    var description: String? = KEY_BLANK
    var startDateTime: String? = KEY_BLANK
    var endDateTime: String? = KEY_BLANK
    var deeplink: String? = KEY_BLANK


}