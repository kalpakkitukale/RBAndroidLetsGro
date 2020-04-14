package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.request.CoursesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CoursesController {

    @POST("getCourseListsStudent")
    suspend fun getCourses(@Body coursesRequest: CoursesRequest): Response<List<CoursesModel>>
}