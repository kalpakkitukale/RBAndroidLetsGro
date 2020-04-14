package com.ramanbyte.emla.data_layer.network.api_layer

import com.amazonaws.services.cognitoidentityprovider.model.ForgotPasswordRequest
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.emla.models.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

interface eMlaApiController {

    /*
    * Master Api's
    * */

    @POST("Login/GetLogin")
    suspend fun doLogin(@Body loginRequest: LoginRequest): Response<UserModel>

    @POST("Login/ForgetPassword")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<String>

    /*
    * Master Api's Ends
    * */

    @POST("Course/getCourseListsStudent")
    suspend fun getCourses(@Body coursesRequest: CoursesRequest): Response<List<CoursesModel>>

    /*
    * Master Api's
    * */

    /*
    * Pre-assessment test courseid= something, QuiztypeId = 1
    * summative test courseid= something, QuiztypeId = 2
    * */
    @GET("Question/GetQuestionListBycourse/{CourseId}/{QuiztypeId}")
    suspend fun getQuestionsByCourse(@Path("CourseId") CourseId: Int, @Path("QuiztypeId") QuiztypeId: Int): Response<ArrayList<QuestionAndAnswerModel>>

    /*
    * Formative test topicId = something, QuiztypeId = 3
    * */
    @GET("Question/GetQuestionListByTopicID/{TopicId}/{QuiztypeId}")
    suspend fun getQuestionsByTopic(@Path("TopicId") TopicId: Int, @Path("QuiztypeId") QuiztypeId: Int): Response<ArrayList<QuestionAndAnswerModel>>

    /*
    * Pre-assessment test topicId = 0, courseid= something, QuiztypeId = 1
    * summative test topicId = 0, courseid= something, QuiztypeId = 2
    * Formative test topicId = something, courseid= something, QuiztypeId = 3
    * */
    @GET("Question/GetInstructions/{topicId}/{courseid}/{QuiztypeId}")
    suspend fun getInstructions(
        @Path("topicId") topicId: Int, @Path("courseid") courseid: Int, @Path(
            "QuiztypeId"
        ) QuiztypeId: Int
    ): Response<InstructionsModel>

//    @POST("Question/SubmitQuiz")
//    suspend fun submitTest(@Body testSubmitModel: QuizmarksModel): Response<QuizResultModel>
//
//    @GET("Question/getQuestionListswithPagination/{EmpId}/{quizId}/{status}/{pageno}/{PageSize}/{attemptStatus}")
//    suspend fun getQuestionForQuizReview(
//        @Path("EmpId") EmpId: Int, @Path("quizId") quizId: Int, @Path("status") status: String, @Path("pageno") pageno: Int, @Path("PageSize") PageSize: Int, @Path("attemptStatus") attemptStatus: Int
//    ): Response<ArrayList<QuizReviewModel>>

    /*
    * Master Api's Ends
    * */

//    @POST("Chapter/getChapterLists")
//    suspend fun getChapterList(@Body chapterRequest: ChapterRequest): Response<List<ChapterModel>>
//
//    @POST("Section/GetAllSection")
//    suspend fun getChapterContentList(@Body chapterContentRequest: ChapterContentRequest): Response<List<ChapterContentModel>>
//
//    @PUT("Login/UpdateStatus")
//    suspend fun updatePledgeStatus(@Body pledgeStatusRequest: PledgeStatusRequest): Response<Int>
//
//    @GET("Course/GetCourseInfoWthihtml/{CourseId}/{userId}")
//    suspend fun getCoursesSyllabus(@Path("CourseId") courseId: Int,@Path("userId") userId : Int): Response<CourseSyllabusModel>
//
//
//    @GET("Section/GetSectionContent/{sectionId}/{userId}")
//    suspend fun getContent(@Path("sectionId") sectionId: Int, @Path("userId") userId: Int): Response<ArrayList<ContentModel>>
//
//    @GET("Question/GetResult/{userId}/{courseId}")
//    suspend fun getCourseResult(@Path("courseId") courseId: Int, @Path("userId") userId: Int): Response<ArrayList<CourseResultModel>>
//
//    @GET("Chapter/GetListOfVideos/{chapterId}")
//    suspend fun getChapterContentList(@Path("chapterId") chapterId: Int): Response<ArrayList<ContentModel>>
//
//    @POST("Login/InsertLogout/{userId}")
//    suspend fun insertLogout(@Body manageUserDeviceModel: ManageUserDeviceModel, @Path("userId") userId: Int): Response<Int>
//
//    @POST("Login/UpdateLogout/{userId}")
//    suspend fun updateLogout(@Body manageUserDeviceModel: ManageUserDeviceModel, @Path("userId") userId: Int): Response<Int>
//
//    @POST("Section/InsertSectionContentLog")
//    suspend fun insertSectionContentLog(@Body mediaInfoEntity: MediaInfoEntity): Response<Int>

}