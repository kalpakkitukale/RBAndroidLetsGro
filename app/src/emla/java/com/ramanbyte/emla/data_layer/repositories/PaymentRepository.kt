package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.PaymentController
import com.ramanbyte.emla.models.request.PayTmTokenRequestModel
import com.ramanbyte.emla.models.response.PayTMTokenResponseModel
import com.ramanbyte.utilities.AppLog
import org.kodein.di.generic.instance

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 18/3/21
 */
class PaymentRepository(mContext: Context) : BaseRepository(mContext) {

    private val paymentController: PaymentController by instance()
    // get token
    suspend fun getToken(payTmTokenRequestModel: PayTmTokenRequestModel): PayTMTokenResponseModel? {
        val userInfo = applicationDatabase.getUserDao().getCurrentUser()
        payTmTokenRequestModel.apply {
            this.custId = userInfo?.userId.toString()
        }
        AppLog.infoLog("Pibm ${payTmTokenRequestModel.amount  + payTmTokenRequestModel.mid}")
        return apiRequest {
            paymentController.getToken(payTmTokenRequestModel)
        }
        /*AppLog.infoLog("Pibm ${data?.body?.gatewayName}")
        return data*/
    }

    // get transaction status
    suspend fun getStatus(payTmTokenRequestModel: PayTmTokenRequestModel):PayTMTokenResponseModel? {
        val  userInfo = applicationDatabase.getUserDao().getCurrentUser()
        payTmTokenRequestModel.apply {
            this.custId = userInfo?.userId.toString()
        }
        return apiRequest {
            paymentController.getStatus(payTmTokenRequestModel)
        }
    }

}