package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.JobRequestModel
import com.ramanbyte.emla.models.request.SkillsRequestModel
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.emla.models.response.SkillsModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SkillsController {

    @POST("GetSkillsWithNoOfJobs")
    suspend fun getSkillsList(@Body request: SkillsRequestModel): Response<List<SkillsModel>>
}