package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.response.CourseQuizModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface CourseQuizController {

    @POST("GetQuizListForCourse")
    suspend fun getQuizListForCourse(
        @Query("UserId") userId: Int,
        @Query("CourseId") courseId: Int,
        @Query("PageNo") pageNo: Int,
        @Query("PageSize") pageSize: Int
    ): Response<ArrayList<CourseQuizModel>>
}