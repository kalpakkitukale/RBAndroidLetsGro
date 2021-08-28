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

interface JobSkillsController {

    @POST("getChapterLists")
    suspend fun getSkillsList(@Body request: SkillsRequestModel): Response<List<SkillsModel>>

    @POST("getChapterLists")
    suspend fun getJobsList(@Body request: JobRequestModel): Response<List<JobModel>>

    @GET("getJobDetails/{jobId}")
    suspend fun getJobDetails(@Path("jobId") jobId: Int): Response<JobModel>
}