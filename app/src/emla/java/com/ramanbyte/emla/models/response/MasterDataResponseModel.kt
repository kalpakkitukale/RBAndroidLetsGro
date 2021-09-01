package com.ramanbyte.emla.models.response

import com.google.gson.annotations.SerializedName

class MasterDataResponseModel {

    @SerializedName("valuField")
    var id = 0

    @SerializedName("textField")
    var itemName = ""
}