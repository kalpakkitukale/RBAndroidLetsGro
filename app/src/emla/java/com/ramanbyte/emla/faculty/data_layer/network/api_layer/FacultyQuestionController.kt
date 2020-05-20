package com.ramanbyte.emla.faculty.data_layer.network.api_layer

import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.models.request.FacultyCoursesRequestModel
import com.ramanbyte.emla.faculty.models.request.StudentAskedQuestionsRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FacultyQuestionController {
    @POST("GetQuestionListByCourseid")
    suspend fun getQuestionListByCourse(@Body coursesRequest: StudentAskedQuestionsRequestModel): Response<List<StudentAskedQuestionsModel>>
}