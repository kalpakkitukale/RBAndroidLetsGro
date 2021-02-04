package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiController {

    // Creating Payment Controller
    @POST("CreateTransactionDetails")
    suspend fun postTransactionDetails(@Body insertTransactionRequestModel: InsertTransactionRequestModel): Response<Int>
}