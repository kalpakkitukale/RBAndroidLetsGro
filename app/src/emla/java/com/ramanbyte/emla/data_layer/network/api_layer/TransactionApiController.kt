package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.TransactionHistoryModel
import com.ramanbyte.emla.models.request.CartRequestModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.emla.models.response.CartResponseModel
import retrofit2.Response
import retrofit2.http.*

interface TransactionApiController {

    // Creating Payment Controller
    @POST("CreateTransactionDetails")
    suspend fun postTransactionDetails(@Body insertTransactionRequestModel: InsertTransactionRequestModel): Response<Int>

    @GET("GetAllCart/{UserId}")
    suspend fun getCartList(@Path("UserId") UserId: Int): Response<ArrayList<CartResponseModel>>

    @GET("GetAllTransactionDetails/{UserId}")
    suspend fun getAllTransactionHistory(@Path("UserId") UserId: Int): Response<ArrayList<TransactionHistoryModel>>

    @POST("AddCart")
    suspend fun insertCart(@Body cartRequestModel: CartRequestModel): Response<Int>

    @DELETE("DeleteCart/{Id}/{UserId}")
    suspend fun deleteCart(@Path("UserId") UserId: Int, @Path("Id") cartItemId: Int): Response<Int>

    @POST("GetAllMyCourses")
    suspend fun mycourseList(@Body coursesRequest: CoursesRequest): Response<List<CoursesModel>>
}