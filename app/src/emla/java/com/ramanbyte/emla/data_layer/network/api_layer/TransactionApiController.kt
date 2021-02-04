package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.request.CartRequestModel
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.emla.models.response.CartResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionApiController {

    // Creating Payment Controller
    @POST("CreateTransactionDetails")
    suspend fun postTransactionDetails(@Body insertTransactionRequestModel: InsertTransactionRequestModel): Response<Int>

    @GET("GetAllCart/{UserId}")
    suspend fun getCartList(@Path("UserId") UserId: Int): Response<ArrayList<CartResponseModel>>

    @POST("AddCart")
    suspend fun insertCart(@Body cartRequestModel: CartRequestModel): Response<Int>
}