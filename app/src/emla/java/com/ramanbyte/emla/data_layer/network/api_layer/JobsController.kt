package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.JobRequestModel
import com.ramanbyte.emla.models.response.JobModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobsController {

    @POST("getChapterLists")
    suspend fun getJobsList(@Body request: JobRequestModel): Response<List<JobModel>>
}