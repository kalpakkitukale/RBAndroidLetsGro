package com.ramanbyte.emla.faculty.data_layer.network.api_layer

import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.models.request.FacultyCoursesRequestModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.request.CoursesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FacultyMasterApiController {
    @POST("getAllocatedCourses")
    suspend fun getCourses(@Body coursesRequest: FacultyCoursesRequestModel): Response<List<FacultyCoursesModel>>
}