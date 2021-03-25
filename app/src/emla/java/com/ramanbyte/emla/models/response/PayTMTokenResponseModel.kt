package com.ramanbyte.emla.models.response

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 18/3/21
 */
class PayTMTokenResponseModel {
    val head = Head()
    val body = Body()

}

class Head{
    var responseTimestamp = ""
    var version = ""
    var signature = ""
}

class Body{
    var resultInfo = ResultInfo()
    var txnId : String = ""
    var bankTxnId : String = ""
    var orderId : String = ""
    var txnAmount : String = ""
    var txnType : String = ""
    var txnToken : String = ""
    var gatewayName : String = ""
    var bankName : String = ""
    var mid : String = ""
    var paymentMode : String = ""
    var refundAmt : String = ""
    var txnDate : String = ""
}

class ResultInfo{
    val resultStatus :String = ""
    val resultCode :String = ""
    val resultMsg :String = ""
}