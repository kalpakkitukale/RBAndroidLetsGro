package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.QuestionController
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.emla.models.InstructionsModel
import org.kodein.di.generic.instance

class QuizRepository (val mContext: Context) : BaseRepository(mContext){

    private val applicationDatabase: ApplicationDatabase by instance()
    private val questionController: QuestionController by instance()

    suspend fun getInstructions(topicId: Int, courseid: Int, QuiztypeId: Int): InstructionsModel? {
        return apiRequest {
            questionController.getInstructions(topicId, courseid, QuiztypeId)
        }
    }

}