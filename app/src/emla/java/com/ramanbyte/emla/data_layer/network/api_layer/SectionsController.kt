package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.SectionsModel
import com.ramanbyte.emla.models.request.SectionsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SectionsController {

    @POST("GetAllSection")
    suspend fun getList(@Body sectionRequest: SectionsRequest): Response<List<SectionsModel>>

    @GET("GetSectionContent/{sectionId}/{userId}")
    suspend fun getContentList(@Path("sectionId") sectionId: Int, @Path("userId") userId: Int): Response<ArrayList<ContentModel>>

}