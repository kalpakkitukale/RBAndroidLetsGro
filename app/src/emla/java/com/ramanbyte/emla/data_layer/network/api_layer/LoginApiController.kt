package com.ramanbyte.emla.data_layer.network.api_layer

import com.amazonaws.services.cognitoidentityprovider.model.ForgotPasswordRequest
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.*
import retrofit2.Response
import retrofit2.http.*

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

interface LoginApiController {

    @POST("GetLogin")
    suspend fun doLogin(@Body loginRequest: LoginRequest): Response<UserModel>

    @POST("ForgetPassword")
    suspend fun forgotPassword(@Body forgetPasswordModel: ForgetPasswordModel): Response<String>

    @POST("ChangePassword")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel):Response<String>

    @PUT("UpdateStatus")
    suspend fun updatePledgeStatus(@Body pledgeStatusRequest: PledgeStatusRequest): Response<Int>

}