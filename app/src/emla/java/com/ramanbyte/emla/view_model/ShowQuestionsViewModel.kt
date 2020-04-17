package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.data_layer.repositories.QuizRepository
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.models.*
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import kotlinx.coroutines.delay
import org.kodein.di.generic.instance

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class ShowQuestionsViewModel(var mContext: Context) : BaseViewModel(mContext) {

    val quizRepository: QuizRepository by instance()

    var coursesModelLiveData: MutableLiveData<CoursesModel> = MutableLiveData()
    var chapterModelLiveData: MutableLiveData<ChaptersModel> = MutableLiveData()
    var testType = 0

    // ------- Instruction Page ----------
    val onClickStartQuizLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isQuizFoundLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val instructionsModelLiveData: MutableLiveData<InstructionsModel> = MutableLiveData()

    // ------- Quiz Page ----------
    val onClickPreviousLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onClickNextLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val questionAndAnswerListLiveData: MutableLiveData<ArrayList<QuestionAndAnswerModel>?> =
        MutableLiveData(null)

    val questionAndAnswerModelLiveData =
        MutableLiveData<ArrayList<QuestionAndAnswerModel>>().apply {
            value = arrayListOf()
        }

    val isJumpToQuestionBS = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onClickClear = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onClickQueNo = MutableLiveData<Int>().apply {
        value = -1
    }

    val isTestSubmited = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isTestSubmitedFormJBS = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onClickCloseLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isQuizSubmited = MutableLiveData<QuizResultModel>().apply {
        value = null
    }

    //---------------- QuizReview -----------------------
    var onClickDialogCorrectQuestionLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var onClickDialogIncorrectQuestionLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var onClickDialogOkLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var onClickTabButtonLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var quizResultModel: QuizResultModel? = null

    var tabTypeSelectedLiveData = MutableLiveData<String>().apply {
        value = keyCorrect
    }

    override var noInternetTryAgain: () -> Unit = {
        if (tabTypeSelectedLiveData.value == keyCorrect) {
            quizRepository.retryQuestionForQuizReview(
                quizResultModel?.quizid!!,
                keyCorrect,
                quizResultModel?.attemptstatus!!
            )
        } else {
            quizRepository.retryQuestionForQuizReview(
                quizResultModel?.quizid!!,
                keyWrong,
                quizResultModel?.attemptstatus!!
            )
        }
    }


    /*
    * Instruction Page ------------- Start ---------------------
    * */

    fun onClickStartQuiz(view: View) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            onClickStartQuizLiveData.value = true
        } else {
            noInternetDialog(BindingUtils.string(R.string.next), view)
        }
    }

    fun getInstructions() {

        invokeApiCall {
            try {
                instructionsModelLiveData.postValue(
                    quizRepository.getInstructions(
                        chapterModelLiveData.value?.chapterId ?: 0,
                        coursesModelLiveData.value?.courseId!!,
                        testType
                    )!!
                )
            } catch (e: NoDataException) {
                isQuizFoundLiveData.postValue(true)
                e.printStackTrace()
                e.message
            }

        }
    }

    /*
    * Instruction Page ------------- End ---------------------
    * */

    private fun noInternetDialog(btnName: String, view: View) {
        CoroutineUtils.main {
            //Showing Alert Dialog for No internet data
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.no_internet_message),
                BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                false,
                BindingUtils.string(R.string.try_again), {
                    if (btnName.equals(BindingUtils.string(R.string.previous))) {
                        onClickPrevious(view)
                    } else {
                        onClickNext(view)
                    }
                    isAlertDialogShown.postValue(false)
                },
                negativeButtonClickFunctionality = {
                    isAlertDialogShown.postValue(false)
                }
            )
            delay(200)
            isAlertDialogShown.postValue(true)
        }
    }

    /*
    * Quiz Page ------------- Start ---------------------
    * */

    fun onClickPrevious(view: View) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            onClickPreviousLiveData.value = true

        } else {
            noInternetDialog(BindingUtils.string(R.string.previous), view)
        }
    }

    fun onClickNext(view: View) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            onClickNextLiveData.value = true
        } else {
            noInternetDialog(BindingUtils.string(R.string.next), view)
        }
    }

    fun getQuestionsByCourse() {
        invokeApiCall(false) {
            questionAndAnswerModelLiveData.postValue(
                quizRepository.getQuestionsByCourse(
                    coursesModelLiveData.value?.courseId!!,
                    testType
                )
                /*quizRepository.getQuestionsByCourse(
                    3028,
                    1
                )*/
            )

            val questionAndAnswerModel = quizRepository.getAllQuestion()
            questionAndAnswerModel?.apply {
                if (this != null && this.size > 0) {
                    questionAndAnswerListLiveData.postValue(this)
                } else
                    AppLog.infoLog("no_question_available")
            }
        }
    }

    fun getQuestionsByByTopic() {
        invokeApiCall {
            questionAndAnswerModelLiveData.postValue(
                quizRepository.getQuestionsByTopic(
                    chapterModelLiveData.value?.chapterId!!,
                    testType
                )
            )

            val questionAndAnswerModel = quizRepository.getAllQuestion()
            questionAndAnswerModel?.apply {
                if (this != null && this.size > 0) {
                    questionAndAnswerListLiveData.postValue(this)
                } else
                    AppLog.infoLog("no_question_available")
            }
        }
    }

    /*
    * Open bottom sheet to jump on particular Question
    * */
    fun onClickJumpToQuestion(view: View) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            isJumpToQuestionBS.value = true
        } else {
            noInternetDialog(BindingUtils.string(R.string.next), view)
        }
    }

    fun getOptions(questionId: Int): ArrayList<OptionsModel>? {

        return quizRepository.getQuestionRelatedOptions(questionId)?.apply {

            val answerModel = quizRepository.getAnswerForQuestion(questionId)

            answerModel?.also { answerModel ->

                firstOrNull { it.id == answerModel.answer }?.apply {
                    isChecked = true
                }

            }
        }
    }

    fun insertOptionLB(answerEntity: AnswerEntity) {
        quizRepository.insertOptionLB(answerEntity)
    }

    fun isQuestionAttempted(question_Id: Int): Int? {
        return quizRepository.isQuestionAttempted(question_Id)
    }

    fun deleteQuestionRelatedOptionLB(question_Id: Int) {
        quizRepository.deleteQuestionRelatedOptionLB(question_Id)
    }

    fun submitTest() {
        invokeApiCall {
            isQuizSubmited.postValue(
                quizRepository.submitTest(
                    coursesModelLiveData?.value?.courseId ?: 0,
                    chapterId = if (KEY_QUIZ_TYPE_FORMATIVE == testType) chapterModelLiveData?.value?.chapterId
                        ?: 0 else 0,
                    testType = testType
                )
            )
        }
    }

    fun getTotalQuestionCount(): Int = quizRepository.getTotalQuestionCount()

    fun getAllQuestions(): List<QuestionAndAnswerModel> = quizRepository.getAllQuestions()

    /*
    * Quiz Page ------------- End ---------------------
    * */


    /*
    * jump to particular questione ------------- start ---------------------
    * */
    fun onClickQueNo(view: View, position: Int) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            onClickQueNo.postValue(position)
        } else {
            noInternetDialog(BindingUtils.string(R.string.next), view)
        }
    }

    fun onClickClose(view: View) {
        onClickCloseLiveData.value = true
    }

    fun onClickSubmitTest(view: View) {

        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.submit_test_message),
            BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
            false,
            BindingUtils.string(R.string.yes), {
                isAlertDialogShown.postValue(false)
                isTestSubmited.value = true
            },
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }
        )
        isAlertDialogShown.postValue(true)
    }

    /*
    * jump to particular question ------------- End ---------------------
    * */


    /*
    * question review ------------- Start ---------------------
    * */

    fun onClickTabButton(view: View, tabType: String, quizResultModel: QuizResultModel) {
        this.quizResultModel = quizResultModel

        onClickTabButtonLiveData.value = tabType
        AppLog.infoLog("quizResultModel_quizid ${quizResultModel.quizid}")
    }

    fun getQuestionForQuizReview(quizid: Int, status: String, attemptstatus: Int) {

        quizRepository.initiatePagination(quizid, status, attemptstatus)

        quizRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {

                paginationResponse(
                    it,
                    //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                    PaginationMessages(
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }
    }

    fun questionForQuizReviewPagedList(): LiveData<PagedList<QuizReviewModel>>? {
        return quizRepository.questionForQuizReviewPagedList
    }

    fun onClickDialogCorrectQuestion(view: View, questionStatus: String) {
        onClickDialogCorrectQuestionLiveData.value = questionStatus
    }

    fun onClickDialogIncorrectQuestion(view: View, questionStatus: String) {
        onClickDialogIncorrectQuestionLiveData.value = questionStatus
    }

    fun onClickDialogOk(view: View, questionStatus: String) {
        onClickDialogOkLiveData.value = questionStatus
    }

    /*
    * question review ------------- End ---------------------
    * */


}