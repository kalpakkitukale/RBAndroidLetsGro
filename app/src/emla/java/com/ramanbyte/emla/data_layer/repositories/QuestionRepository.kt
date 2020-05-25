package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.SharedPreferencesDatabase.getIntPref
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.QuestionController
import com.ramanbyte.emla.data_layer.network.api_layer.SectionsController
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.models.request.AskQuestionRequestModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.models.request.ConversationCloseRequestModel
import com.ramanbyte.emla.models.request.QuestionsReplyRequestModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_DEVICE_ID
import org.kodein.di.generic.instance

class QuestionRepository(mContext: Context) : BaseRepository(mContext) {

    private val questionController: QuestionController by instance()
    private val sectionsController: SectionsController by instance()

    suspend fun insertAskQuestion(
        mediaInfoModel: MediaInfoModel,
        question: String
    ): AskQuestionRequestModel? {

        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

        val askQuestionModel = AskQuestionRequestModel()
            .apply {
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

    suspend fun insertQuestionsReply(
        currentQuestionId: Int,
        userAnswer: String
    ): QuestionsReplyRequestModel? {

        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

        val questionsReplyRequestModel = QuestionsReplyRequestModel()
            .apply {
                question_Id = currentQuestionId
                this.userId = userId
                answer = userAnswer
            }

        return apiRequest {
            questionController.insertQuestionsReply(questionsReplyRequestModel)
        }
    }

    suspend fun getQuestionAndAnswer(contentId: Int): ArrayList<AskQuestionModel>? {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return apiRequest {
            questionController.getQuestionAndAnswer(userId, contentId)
        }
    }

    suspend fun getConversationData(questionId: Int): ArrayList<AskQuestionReplyModel>? {
        return apiRequest {
            questionController.getConversationData(questionId)
        }
    }

    /*
    * Insert or Update the Conversation status
    * */
    suspend fun updateConversationCloseStatus(
        questionId: Int
    ): String? {
        val conversationCloseRequestModel = ConversationCloseRequestModel()
            .apply {
                this.questionId = questionId
            }
        return apiRequest {
            questionController.updateConversationCloseStatus(conversationCloseRequestModel)
        }
    }

    suspend fun getFavouriteVideos(): ArrayList<FavouriteVideosModel>? {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return apiRequest {
            questionController.getFavouriteVideos(userId)
        }
    }

    fun getMediaInfo(mediaId: Int): MediaInfoModel? {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase.getMediaInfoDao().getMediaInfo(mediaId, userId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) {
        applicationDatabase.getMediaInfoDao().update(mediaInfoModel)
    }

    /*
    * call the api to insert SectionContentLog to the server
    * */
    suspend fun insertSectionContentLog(
        mediaId: Int
    ): Int {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        var result: Int? = 0

        /*
        * get all the record from local database which is not sync to server
        * */
        val allMediaInfo = applicationDatabase.getMediaInfoDao().getMediaInfo(mediaId, userId)

        allMediaInfo?.apply {
            this.createdBy = userId
            this.modifiedBy = userId
            this.device_Id = getIntPref(KEY_DEVICE_ID)
            result = apiRequest {
                sectionsController.insertSectionContentLog(allMediaInfo)
            }!!
            if (result!! > 0) {
                /*
                * if data is inserted to server then change then result!! > 0
                * */
                AppLog.infoLog("dataInserted")
            }
        }
        return result!!
    }


    /*
    * call the api to insert Favourite Video Status to the server
    * */
    suspend fun insertFavouriteVideoStatus(
        favouriteVideosModel: FavouriteVideosModel
    ): Int {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        var result: Int? = 0

        val allMediaInfo = MediaInfoModel().apply {
            mediaId = favouriteVideosModel.contentId
            courseId = favouriteVideosModel.courseId
            chapterId = favouriteVideosModel.chapterId
            sectionId = favouriteVideosModel.sectionId
            mediaType = favouriteVideosModel.contentType
            mediaStatus = favouriteVideosModel.status.toInt()
            device_Id = favouriteVideosModel.deviceId
            seekPosition = favouriteVideosModel.seekPosition.toLong()
            likeVideo = favouriteVideosModel.isLikeVideo
            favouriteVideo = favouriteVideosModel.isFavouriteVideo
            this.createdBy = userId
            this.modifiedBy = userId
            this.userId = userId
            this.device_Id = getIntPref(KEY_DEVICE_ID)
        }


        result = apiRequest {
            sectionsController.insertSectionContentLog(allMediaInfo)
        }!!
        if (result!! > 0) {
            /*
            * if data is inserted to server then change then result!! > 0
            * */
            AppLog.infoLog("dataInserted")
        }

        return result!!
    }




}