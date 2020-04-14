package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.CourseSyllabusModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoursesController {

    @GET("Course/GetCourseInfoWthihtml/{CourseId}/{userId}")
    suspend fun getCoursesSyllabus(@Path("CourseId") courseId: Int, @Path("userId") userId : Int): Response<CourseSyllabusModel>
}