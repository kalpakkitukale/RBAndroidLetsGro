package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.CityModel
import com.ramanbyte.emla.models.CountryModel
import com.ramanbyte.emla.models.RegistrationModel
import com.ramanbyte.emla.models.StateModel
import com.ramanbyte.emla.models.response.MasterDataResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
interface RegistrationController {

    @POST("InsertRegistrationDetails")
    suspend fun register(@Body registrationModel: RegistrationModel): Response<String>

    @GET("GetProfileDetails/{userReffId}")
    suspend fun getProfile(@Path("userReffId") userReffId: Int): Response<RegistrationModel>

    @POST("InsertUpdateProfileDetails")
    suspend fun updateLearnerProfile(@Body registrationModel: RegistrationModel): Response<String>

    @GET("GetAllCountrys")
    suspend fun getCountries(): Response<ArrayList<CountryModel>>

    @GET("GetStateById/{countryId}")
    suspend fun getStates(@Path("countryId") countryId: Int): Response<ArrayList<StateModel>>

    @GET("GetAllCityById/{stateId}")
    suspend fun getCities(@Path("stateId") stateId: Int): Response<ArrayList<CityModel>>

    @GET("GetProgramLevalDetails")
    suspend fun getProgramLevels(): Response<ArrayList<MasterDataResponseModel>>

    @GET("GetPatternDetails")
    suspend fun getEducationPatterns(): Response<ArrayList<MasterDataResponseModel>>

    @GET("GetSpecializationDetails")
    suspend fun getSpecializations(): Response<ArrayList<MasterDataResponseModel>>
}