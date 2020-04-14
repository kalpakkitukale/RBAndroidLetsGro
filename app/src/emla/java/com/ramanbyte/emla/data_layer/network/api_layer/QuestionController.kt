package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.models.QuizResultModel
import com.ramanbyte.emla.models.QuizmarksModel
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

}