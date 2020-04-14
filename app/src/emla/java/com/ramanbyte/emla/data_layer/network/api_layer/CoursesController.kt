package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.CourseSyllabusModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.request.CoursesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoursesController {

    @POST("getCourseListsStudent")
    suspend fun getCourses(@Body coursesRequest: CoursesRequest): Response<List<CoursesModel>>

    @GET("GetCourseInfoWthihtml/{CourseId}/{userId}")
    suspend fun getCoursesSyllabus(@Path("CourseId") courseId: Int, @Path("userId") userId : Int): Response<CourseSyllabusModel>

}