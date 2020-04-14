package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.QuestionController
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.models.OptionsModel
import com.ramanbyte.utilities.AppLog
import org.kodein.di.generic.instance

class QuizRepository (val mContext: Context) : BaseRepository(mContext){

    private val applicationDatabase: ApplicationDatabase by instance()
    private val questionController: QuestionController by instance()

    suspend fun getInstructions(topicId: Int, courseid: Int, QuiztypeId: Int): InstructionsModel? {
        return apiRequest {
            questionController.getInstructions(topicId, courseid, QuiztypeId)
        }
    }

    /*
    * After selecting an option, update into the table
    * */
    fun insertOptionLB(answerEntity: AnswerEntity) {

        /*applicationDatabase.getAnswerDao().apply {

            AppLog.infoLog("Quiz Id :: ${answerEntity.question_Id} Options Size:: ${getAllQuestionRelatedOption().size}")

            deleteQuestionRelatedOption(answerEntity.question_Id)
            insert(answerEntity)
        }*/
    }

    fun getQuestionRelatedOptions(questionId: Int): ArrayList<OptionsModel>? {
        val optionsList: ArrayList<OptionsModel> = ArrayList()
        /*applicationDatabase.getOptionsDao().getQuestionRelatedOptions(questionId).forEach {
            optionsList.add(it?.replicate<OptionsEntity, OptionsModel>()!!)
        }*/
        return optionsList
    }



}