package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.request.ApplyJobRequestModel
import com.ramanbyte.emla.models.request.JobRequestModel
import com.ramanbyte.emla.models.response.ApplyJobResponseModel
import com.ramanbyte.emla.models.response.JobModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JobsController {

    @POST("GetJobs")
    suspend fun getJobsList(@Body request: JobRequestModel): Response<List<JobModel>>

    @POST("GetFilterJobs")
    suspend fun getFilterJobs(@Body request: JobRequestModel): Response<List<JobModel>>

    @POST("Viewjobs")
    suspend fun getJobDetails(@Body request: JobRequestModel): Response<List<JobModel>>

    @POST("ApplyJob")
    suspend fun applyJob(@Body request: ApplyJobRequestModel): Response<ApplyJobResponseModel>

}