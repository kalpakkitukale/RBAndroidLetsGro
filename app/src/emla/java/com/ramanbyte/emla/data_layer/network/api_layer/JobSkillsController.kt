package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.SkillsRequestModel
import com.ramanbyte.emla.models.response.SkillsModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobSkillsController {
    /*
    * POST getChapterLists
    * */
    @POST("getChapterLists")
    suspend fun getSkillsList(@Body request: SkillsRequestModel): Response<List<SkillsModel>>
}