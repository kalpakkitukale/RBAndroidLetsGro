package com.ramanbyte.emla.models

import com.payu.india.Model.PaymentParams
import com.payu.india.Model.PayuConfig
import com.payu.india.Model.PayuHashes

class PayuGatewayModel {

    var payuHashes: PayuHashes = PayuHashes()
    var mPaymentParams: PaymentParams = PaymentParams()
    var payuConfig: PayuConfig = PayuConfig()
}