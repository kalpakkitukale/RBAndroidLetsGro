package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.SharedPreferencesDatabase.getIntPref
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.QuestionController
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_DEVICE_ID
import org.kodein.di.generic.instance

class QuestionRepository(mContext: Context) : BaseRepository(mContext) {

    private val questionController: QuestionController by instance()

    suspend fun insertAskQuestion(
        mediaInfoModel: MediaInfoModel,
        question: String
    ): AskQuestionModel? {

        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

        val askQuestionModel = AskQuestionModel().apply {
            student_Id = userId
            course_Id = mediaInfoModel.courseId
            chpater_Id = mediaInfoModel.chapterId
            section_Id = mediaInfoModel.sectionId
            content_Id = mediaInfoModel.mediaId
            faculty_Id = 0
            content_Type = mediaInfoModel.mediaType
            device_Id = getIntPref(KEY_DEVICE_ID).toString()
            this.question = question
            answer = KEY_BLANK
        }

        return apiRequest {
            questionController.insertAskQuestion(askQuestionModel)
        }
    }

}