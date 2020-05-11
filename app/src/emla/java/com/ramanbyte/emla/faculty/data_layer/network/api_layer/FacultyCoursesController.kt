package com.ramanbyte.emla.faculty.data_layer.network.api_layer

import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.request.CoursesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FacultyCoursesController {
    @POST("getrecomdedcourselist")
    suspend fun getCourses(@Body coursesRequest: CoursesRequest): Response<List<CoursesModel>>
}