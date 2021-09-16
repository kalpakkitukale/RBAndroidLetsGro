package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.QuizResultModel
import com.ramanbyte.emla.models.QuizmarksModel
import com.ramanbyte.emla.models.response.CourseQuizModel
import com.ramanbyte.emla.models.response.CourseQuizResultModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    @POST("GetQuizResult")
    suspend fun getQuizResult(
        @Query("UserId") userId: Int,
        @Query("QuizId") courseId: Int
    ): Response<ArrayList<CourseQuizResultModel>>

    @GET("GetServerDateTime")
    suspend fun getServerDateTime(): Response<String>

    @POST("SubmitCourseQuiz")
    suspend fun submitTest(@Body testSubmitModel: QuizmarksModel): Response<QuizResultModel>
}