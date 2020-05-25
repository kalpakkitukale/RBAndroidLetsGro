package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.*
import com.ramanbyte.emla.models.request.AskQuestionRequestModel
import com.ramanbyte.emla.models.request.ConversationCloseRequestModel
import com.ramanbyte.emla.models.request.QuestionsReplyRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuestionController {



    /*
   * Pre-assessment test topicId = 0, courseid= something, QuiztypeId = 1
   * summative test topicId = 0, courseid= something, QuiztypeId = 2
   * Formative test topicId = something, courseid= something, QuiztypeId = 3
   * */
    @GET("GetInstructions/{topicId}/{courseid}/{QuiztypeId}")
    suspend fun getInstructions(
        @Path("topicId") topicId: Int, @Path("courseid") courseid: Int, @Path(
            "QuiztypeId"
        ) QuiztypeId: Int
    ): Response<InstructionsModel>

    /*
    * Pre-assessment test courseid= something, QuiztypeId = 1
    * summative test courseid= something, QuiztypeId = 2
    * */
    @GET("GetQuestionListBycourse/{CourseId}/{QuiztypeId}")
    suspend fun getQuestionsByCourse(@Path("CourseId") CourseId: Int, @Path("QuiztypeId") QuiztypeId: Int): Response<ArrayList<QuestionAndAnswerModel>>

    /*
    * Formative test topicId = something, QuiztypeId = 3
    * */
    @GET("GetQuestionListByTopicID/{TopicId}/{QuiztypeId}")
    suspend fun getQuestionsByTopic(@Path("TopicId") TopicId: Int, @Path("QuiztypeId") QuiztypeId: Int): Response<ArrayList<QuestionAndAnswerModel>>

    @POST("SubmitQuiz")
    suspend fun submitTest(@Body testSubmitModel: QuizmarksModel): Response<QuizResultModel>

    @GET("getQuestionListswithPagination/{EmpId}/{quizId}/{status}/{pageno}/{PageSize}/{attemptStatus}")
    suspend fun getQuestionForQuizReview(
        @Path("EmpId") EmpId: Int, @Path("quizId") quizId: Int, @Path("status") status: String, @Path("pageno") pageno: Int, @Path("PageSize") PageSize: Int, @Path("attemptStatus") attemptStatus: Int
    ): Response<ArrayList<QuizReviewModel>>

    @GET("GetResult/{userId}/{courseId}")
    suspend fun getCourseResult(@Path("courseId") courseId: Int, @Path("userId") userId: Int): Response<ArrayList<CourseResultModel>>

    @POST("InsertQNA")
    suspend fun insertAskQuestion(@Body askQuestionRequestModel: AskQuestionRequestModel): Response<AskQuestionRequestModel>

    @POST("InsertQNADetails")
    suspend fun insertQuestionsReply(@Body questionsReplyRequestModel: QuestionsReplyRequestModel): Response<QuestionsReplyRequestModel>

    @GET("GetQNA/{studentId}/{contentId}")
    suspend fun getQuestionAndAnswer(@Path("studentId") studentId: Int, @Path("contentId") contentId: Int): Response<ArrayList<AskQuestionModel>>

    @GET("GetConversationData/{questionId}")
    suspend fun getConversationData(@Path("questionId") questionId: Int): Response<ArrayList<AskQuestionReplyModel>>

    @GET("GetFavouriteVideos/{userId}")
    suspend fun getFavouriteVideos(@Path("userId") userId: Int): Response<ArrayList<FavouriteVideosModel>>

    @POST("UpdateConversationCloseStatus")
    suspend fun updateConversationCloseStatus(@Body conversationCloseRequestModel: ConversationCloseRequestModel): Response<String>

}