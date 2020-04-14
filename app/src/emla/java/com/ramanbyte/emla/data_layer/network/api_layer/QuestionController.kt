package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.InstructionsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuestionController {



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

}