package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.JobRequestModel
import com.ramanbyte.emla.models.response.JobModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobsController {

    @POST("GetJobs")
    suspend fun getJobsList(@Body request: JobRequestModel): Response<List<JobModel>>

    @POST("GetFilterJobs")
    suspend fun getFilterJobs(@Body request: JobRequestModel): Response<List<JobModel>>
}