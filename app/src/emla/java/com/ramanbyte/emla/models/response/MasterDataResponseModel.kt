package com.ramanbyte.emla.models.response

import com.google.gson.annotations.SerializedName

class MasterDataResponseModel {

    @SerializedName("valueField")
    var id = 0

    @SerializedName("textField")
    var itemName = ""
}