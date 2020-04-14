package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.data_layer.repositories.QuizRepository
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import kotlinx.coroutines.delay
import org.kodein.di.generic.instance

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class ShowQuestionsViewModel (var mContext: Context) : BaseViewModel(mContext) {
    override var noInternetTryAgain: () -> Unit = {}

    val quizRepository: QuizRepository by instance()

    var coursesModelLiveData: MutableLiveData<CoursesModel> = MutableLiveData()
    //var chapterModelLiveData: MutableLiveData<ChapterModel> = MutableLiveData()
    var testType = 0

    // ------- Instruction Page ----------
    val onClickStartQuizLiveData = MutableLiveData<Boolean>().apply {
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

    val questionAndAnswerListLiveData: MutableLiveData<ArrayList<QuestionAndAnswerModel>> =
        MutableLiveData()

    val questionAndAnswerModelLiveData =
        MutableLiveData<ArrayList<QuestionAndAnswerModel>>().apply {
            value = arrayListOf()
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
            instructionsModelLiveData.postValue(
                /*quizRepository.getInstructions(
                    chapterModelLiveData.value?.chapterId ?: 0,
                    coursesModelLiveData.value?.courseId!!,
                    testType
                )!!*/
                quizRepository.getInstructions(
                    45,
                    coursesModelLiveData.value?.courseId!!,
                    2
                )!!
            )
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
        /*invokeApiCall(false) {
            questionAndAnswerModelLiveData.postValue(
                quizRepository.getQuestionsByCourse(
                    coursesModelLiveData.value?.courseId!!,
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
        }*/
    }

    fun getQuestionsByByTopic() {

        /*invokeApiCall {
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
        }*/
    }

    /*
    * Quiz Page ------------- End ---------------------
    * */


}