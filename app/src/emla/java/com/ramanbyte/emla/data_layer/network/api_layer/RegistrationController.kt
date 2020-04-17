package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.RegistrationModel
import com.ramanbyte.emla.models.response.CommonDropdownModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
interface RegistrationController {

    @POST("InsertRegistrationDetails")
    suspend fun register(@Body registrationModel: RegistrationModel): Response<String>

    @GET("GetProgramLevalDetails")
    suspend fun getAllPrograms(): Response<List<CommonDropdownModel>>

    @GET("GetPatternDetails")
    suspend fun getAllPatterns(): Response<List<CommonDropdownModel>>

    @GET("GetSpecializationDetails")
    suspend fun getAllSpecializations(): Response<List<CommonDropdownModel>>

}