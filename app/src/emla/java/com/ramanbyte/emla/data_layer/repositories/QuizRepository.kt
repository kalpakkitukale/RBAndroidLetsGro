package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.CourseQuizController
import com.ramanbyte.emla.data_layer.network.api_layer.QuestionController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.data_layer.room.entities.OptionsEntity
import com.ramanbyte.emla.data_layer.room.entities.QuestionAndAnswerEntity
import com.ramanbyte.emla.models.*
import com.ramanbyte.emla.models.request.QuizReviewRequestModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class QuizRepository(val mContext: Context) : BaseRepository(mContext) {

    private val questionController: QuestionController by instance()

    private val courseQuizController: CourseQuizController by instance()

    suspend fun getInstructions(
        topicId: Int,
        courseid: Int,
        QuiztypeId: Int,
        quizId: Int
    ): InstructionsModel? {
        return apiRequest {
            questionController.getInstructions(topicId, courseid, QuiztypeId, quizId)
        }
    }

    suspend fun getQuestionsByCourse(
        courseId: Int,
        quizTypeId: Int
    ): ArrayList<QuestionAndAnswerModel>? {
        val questionAndAnswerModel = apiRequest {
            questionController.getQuestionsByCourse(courseId, quizTypeId)
        }

        /*val questionAndAnswerModel = QuestionAndAnswerModel().apply {
            id = 1
        }*/

        deleteQuiz()

        questionAndAnswerModel?.apply {

            //if (applicationDatabase.getQuestionAndAnswerDao().getAllQuestion().isEmpty())
            forEach { questionAndAnswerModel ->
                val questionEntity =
                    questionAndAnswerModel.replicate<QuestionAndAnswerModel, QuestionAndAnswerEntity>()!!

                applicationDatabase.apply {

                    var questionLocalId: Long = 0

                    getQuestionAndAnswerDao()?.apply {
                        questionLocalId = insert(questionEntity)
                    }

                    questionAndAnswerModel.questionDetails.forEach { optionsModel ->

                        val optionsEntity =
                            optionsModel.replicate<OptionsModel, OptionsEntity>()!!

                        getOptionsDao()?.apply {
                            optionsEntity.questionsLocalId = questionLocalId
                            insert(optionsEntity)
                        }

                        insertOptionLB(AnswerEntity().apply {
                            this.quiz_Id = questionAndAnswerModel?.quizid ?: 0
                            this.question_Id = optionsEntity.question_Id
                            this.options = KEY_BLANK
                            this.iscorrect = "S"
                            this.answer = 0
                            this.createdDate = DateUtils.getCurrentDateTime(
                                DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                            )
                        })

                    }
                }
            }
        }
        return questionAndAnswerModel
    }

    fun deleteQuiz() {
        applicationDatabase.apply {
            deleteAllQuestion()
            getOptionsDao().deleteAll()
            getAnswerDao().deleteAll()
        }
    }

    suspend fun getQuestionsByTopic(
        topicId: Int,
        quizTypeId: Int
    ): ArrayList<QuestionAndAnswerModel>? {
        val questionAndAnswerModel = apiRequest {
            questionController.getQuestionsByTopic(topicId, quizTypeId)
        }

        /*val questionAndAnswerModel = QuestionAndAnswerModel().apply {
            id = 1
        }*/

        deleteQuiz()

        questionAndAnswerModel?.apply {

            if (applicationDatabase.getQuestionAndAnswerDao().getAllQuestion().isEmpty())
                forEach { questionAndAnswerModel ->
                    val questionEntity =
                        questionAndAnswerModel.replicate<QuestionAndAnswerModel, QuestionAndAnswerEntity>()!!

                    applicationDatabase.apply {

                        val questionLocalId = getQuestionAndAnswerDao().insert(questionEntity)

                        questionAndAnswerModel.questionDetails.forEach { optionsModel ->

                            val optionsEntity =
                                optionsModel.replicate<OptionsModel, OptionsEntity>()!!

                            getOptionsDao().insert(optionsEntity.apply {
                                this.questionsLocalId = questionLocalId
                            })

                            insertOptionLB(AnswerEntity().apply {
                                this.quiz_Id = questionAndAnswerModel?.quizid ?: 0
                                this.question_Id = optionsEntity.question_Id
                                this.options = KEY_BLANK
                                this.iscorrect = KEY_SKIP
                                this.answer = 0
                                this.createdDate = DateUtils.getCurrentDateTime(
                                    DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                                )
                            })
                        }
                    }
                }
        }
        return questionAndAnswerModel
    }

    suspend fun submitTest(
        courseId: Int,
        chapterId: Int,
        testType: Int,
        courseQuizId: Int
    ): QuizResultModel? {

        //var resultModel = QuizResultModel()
        val quizModel = QuizmarksModel()
        val testSubmitModel = TestSubmitModel().apply {

            applicationDatabase.getQuestionAndAnswerDao().submitAllQuestion()?.also { quiz ->

                employee_Id = applicationDatabase?.getUserDao().getCurrentUser()?.userId ?: 0

                total_Marks =
                    applicationDatabase?.getAnswerDao()?.getTotalObtainedMarks(quiz.quizid, KEY_Y)

                val quizMarks =
                    applicationDatabase?.getQuestionAndAnswerDao().getTotalMarks(quiz.quizid)

                AppLog.infoLog("quiz_marks $total_Marks $quizMarks")

                if (quizMarks > 0) {
                    passing_Percent = ((total_Marks / quizMarks) * 100).toDouble()
                } else {
                    passing_Percent = 0.0
                }

                val format = DecimalFormat("##.##")

                // obtaind per
                passing_Percent = format.format(passing_Percent).toDouble()

                val passingPercentRequired =
                    applicationDatabase?.getQuestionAndAnswerDao().getPassingPercent(quiz.quizid)

                course_Id = courseId
                quiz_Type = testType
                chapter_Id = chapterId
                if (testType == KEY_QUIZ_TYPE_COURSE_QUIZ) {
                    quiz_Id = courseQuizId
                } else {
                    quiz_Id = quiz.quizid
                }
                start_Time =
                    SharedPreferencesDatabase.getStringPref(SharedPreferencesDatabase.KEY_START_QUIZ_DATE_TIME)
                modifiedBy = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
                createdBy = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
                quizsubmissionEntity.apply {
                    //clear()
                    addAll(applicationDatabase.getAnswerDao().getAllQuestionRelatedOption().apply {
                        forEach {
                            it.quiz_Id = quiz.quizid
                            it.employee_Id = employee_Id
                        }
                    })
                }

            }
        }

        quizModel.quizmarks = testSubmitModel

        return if (testType == KEY_QUIZ_TYPE_COURSE_QUIZ) {
            quizModel.quizsubmissionEntity = testSubmitModel.quizsubmissionEntity
            apiRequest {
                courseQuizController.submitTest(quizModel)
            }
        } else {
            apiRequest {
                questionController.submitTest(quizModel)
            }
        }


        //return QuizResultModel()


    }


    //------------- Local data base functions ---------------------------

    fun getAllQuestion(): ArrayList<QuestionAndAnswerModel>? {
        val questionAndAnswerList: ArrayList<QuestionAndAnswerModel> = ArrayList()
        applicationDatabase.getQuestionAndAnswerDao().getAllQuestion().forEach {
            questionAndAnswerList.add(it?.replicate<QuestionAndAnswerEntity, QuestionAndAnswerModel>()!!)
        }
        return questionAndAnswerList
        //?.replicate<QuestionAndAnswerEntity, QuestionAndAnswerModel>()
    }

    fun getQuestionRelatedOptions(questionId: Int): ArrayList<OptionsModel>? {
        val optionsList: ArrayList<OptionsModel> = ArrayList()
        applicationDatabase.getOptionsDao().getQuestionRelatedOptions(questionId).forEach {
            optionsList.add(it?.replicate<OptionsEntity, OptionsModel>()!!)
        }
        return optionsList
    }

    /*
    * After selecting an option, update into the table
    * */
    fun insertOptionLB(answerEntity: AnswerEntity) {

        applicationDatabase.getAnswerDao().apply {

            AppLog.infoLog("Quiz Id :: ${answerEntity.question_Id} Options Size:: ${getAllQuestionRelatedOption().size}")

            deleteQuestionRelatedOption(answerEntity.question_Id)
            insert(answerEntity)
        }
    }

    fun updateOptionLB(answerEntity: AnswerEntity) {
        applicationDatabase.getAnswerDao().update(answerEntity)
    }

    fun getQuestionRelatedOptionCountLB(question_Id: Int): Int {
        return applicationDatabase.getAnswerDao().getQuestionRelatedOptionCount(question_Id)
    }

    fun deleteQuestionRelatedOptionLB(question_Id: Int) {
        applicationDatabase.getAnswerDao().deleteQuestionRelatedOption(question_Id)
    }

    fun deleteAllQuestion() {
        return applicationDatabase.getQuestionAndAnswerDao().deleteAllQuestion()
    }

    fun getAnswerForQuestion(questionId: Int): AnswerEntity? {
        return applicationDatabase.getAnswerDao().getAnswerForQuestion(questionId)
    }

    fun isQuestionAttempted(questionId: Int): Int? {
        return applicationDatabase.getAnswerDao().isQuestionAttempted(questionId)
    }

    fun getTotalQuestionCount(): Int =
        applicationDatabase.getQuestionAndAnswerDao().getTotalQuestionCount() ?: 0

    fun getAllQuestions(): List<QuestionAndAnswerModel> {

        return applicationDatabase?.getQuestionAndAnswerDao().getAllQuestion()?.map {
            it?.replicate<QuestionAndAnswerEntity, QuestionAndAnswerModel>()!!
        }

    }


    //----------------------------------------------------------------------------------------

    var questionForQuizReviewPagedList: LiveData<PagedList<QuizReviewModel>>? = null
    lateinit var questionForQuizReviewPagedDataSourceFactory: PaginationDataSourceFactory<QuizReviewModel, QuizReviewRequestModel>
    private val pageSize = 10
    private val pageListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()

    private val quizReviewRequestModelObservable = ObservableField<QuizReviewRequestModel>().apply {
        set(QuizReviewRequestModel())
    }

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)

    fun initiatePagination(quizId: Int, status: String, attemptstatus: Int) {

        val empId = applicationDatabase.getUserDao().getCurrentUser()?.userId

        quizReviewRequestModelObservable.apply {
            set(QuizReviewRequestModel().apply {
                this.status = status
                this.attemptStatus = attemptstatus
            })
        }

        questionForQuizReviewPagedDataSourceFactory = PaginationDataSourceFactory(
            quizReviewRequestModelObservable,
            paginationResponseHandlerLiveData
        ) { quizReviewModel ->
            apiRequest {
                questionController.getQuestionForQuizReview(
                    empId!!,
                    quizId,
                    quizReviewModel.status,
                    quizReviewModel.pageNo,
                    pageSize,
                    quizReviewModel.attemptStatus
                )
            }!!
        }

        questionForQuizReviewPagedList =
            LivePagedListBuilder(
                questionForQuizReviewPagedDataSourceFactory,
                pageListConfig
            ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData

    fun retryQuestionForQuizReview(quizId: Int, questionStatus: String, attemptstatus: Int) {

        quizReviewRequestModelObservable.apply {
            set(QuizReviewRequestModel().apply {
                this.status = questionStatus
                this.quizId = quizId
                this.attemptStatus = attemptstatus
            })
        }

        questionForQuizReviewPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    suspend fun getCourseResult(courseId: Int): ArrayList<CourseResultModel>? {
        val userId = applicationDatabase?.getUserDao()?.getCurrentUser()?.userId
        return apiRequest {
            questionController.getCourseResult(courseId, userId!!)
        }
    }
}