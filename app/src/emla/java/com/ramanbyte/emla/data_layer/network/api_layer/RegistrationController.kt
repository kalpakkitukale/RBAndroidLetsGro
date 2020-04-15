package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.RegistrationModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
interface RegistrationController {

    @POST("InsertRegistrationDetails")
    suspend fun register(@Body registrationModel: RegistrationModel): Response<String>

}