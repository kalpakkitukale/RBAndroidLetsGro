package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.PayTmTokenRequestModel
import com.ramanbyte.emla.models.response.PayTMTokenResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 18/3/21
 */
interface PaymentController {

    @POST("GetTransactionToken")
    suspend fun getToken(@Body payTmTokenRequestModel: PayTmTokenRequestModel): Response<PayTMTokenResponseModel>

    @POST("GetTransactionStatus")
    suspend fun getStatus(@Body payTmTokenRequestModel: PayTmTokenRequestModel): Response<PayTMTokenResponseModel>


}