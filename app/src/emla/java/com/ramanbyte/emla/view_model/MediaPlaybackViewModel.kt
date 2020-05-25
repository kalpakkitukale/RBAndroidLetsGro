package com.ramanbyte.emla.view_model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.models.request.ConversationCloseRequestModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

class MediaPlaybackViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val contentRepository: ContentRepository by instance()
    private val sectionsRepository: SectionsRepository by instance()
    private val questionRepository: QuestionRepository by instance()

    var mediaInfoModel: MediaInfoModel? = null

    var onClickCloseCommentLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickSendQuestionLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickReplyLiveData = MutableLiveData<AskQuestionModel>().apply {
        value = null
    }

    var enteredQuestionLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var visibilityAddQuestionBtnLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    var questionAndAnswerListLiveData = MutableLiveData<List<AskQuestionModel>>().apply {
        value = arrayListOf()
    }

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = contentRepository.getMediaInfo(mediaId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) =
        contentRepository.updateMediaInfo(mediaInfoModel)

    var conversationCloseLiveData = MutableLiveData<String>().apply {
        value = null
    }

    override var noInternetTryAgain: () -> Unit = {
        getQuestionAndAnswer()
    }


    fun insertSectionContentLog(
        mediaId: Int,
        mediaInfoModel: MediaInfoModel
    ) {
        CoroutineUtils.main {

            try {
                //isLoaderShowingLiveData.postValue(true)
                contentRepository.updateMediaInfo(mediaInfoModel)
                sectionsRepository.insertSectionContentLog(mediaId)

                //isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                //view.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                //view.snackbar(BindingUtils.string(R.string.no_internet_message))
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }

        }
    }


    fun insertAskQuestion(question: String) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                questionRepository.insertAskQuestion(mediaInfoModel!!, question)
                getQuestionAndAnswer()
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                //view.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                //view.snackbar(BindingUtils.string(R.string.no_internet_message))
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
            }

        }
    }

    fun getQuestionAndAnswer() {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_questions_list))

                val response = questionRepository.getQuestionAndAnswer(mediaInfoModel?.mediaId!!)
                questionAndAnswerListLiveData.postValue(response)

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK,
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )
                coroutineToggleLoader()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.ask_questions_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }

    }

    fun onClickCloseComment(view: View) {
        onClickCloseCommentLiveData.value = true
    }


    fun onClickAskNewQuestion(view: View, questionId: Int) {
        //onClickAskNQuestionLiveData.value = true
        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.conversation_close_message),
            BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
            false,
            BindingUtils.string(R.string.yes), {
                isAlertDialogShown.postValue(false)
                AppLog.infoLog("questionId=== $questionId")
                updateConversationCloseStatus(questionId)
                //findNavController().navigateUp()
            },
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }
        )
        isAlertDialogShown.postValue(true)
    }

    private fun updateConversationCloseStatus(questionId: Int) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                val response = questionRepository.updateConversationCloseStatus(questionId)
                conversationCloseLiveData.postValue(response)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                alertMediaPlayBack(BindingUtils.string(R.string.some_thing_went_wrong),BindingUtils.drawable(R.drawable.ic_something_went_wrong)!!,BindingUtils.string(R.string.strOk))
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)

                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    false,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                        updateConversationCloseStatus(questionId)
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                alertMediaPlayBack(BindingUtils.string(R.string.some_thing_went_wrong),BindingUtils.drawable(R.drawable.ic_something_went_wrong)!!,BindingUtils.string(R.string.strOk))
            }

        }
    }

    fun onClickSendQuestion(view: View) {
        onClickSendQuestionLiveData.value = true
    }

    fun onClickReply(view: View, askQuestionModel: AskQuestionModel) {
        onClickReplyLiveData.value = askQuestionModel
    }

    /*
    * Add Reply Page started ---
    * */

    var onClickBackLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var onClickAddReplyLiveData = MutableLiveData<Int>().apply {
        value = 0
    }

    var enteredReplyLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var visibilityAddReplyBtnLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    var questionsReplyListLiveData = MutableLiveData<ArrayList<AskQuestionReplyModel>>().apply {
        value = arrayListOf()
    }

    fun onClickBack(view: View){
        onClickBackLiveData.value = true
    }

    fun onClickAddReply(view: View, questionId:Int) {
        onClickAddReplyLiveData.value = questionId
    }

    fun insertQuestionsReply(questionId: Int, reply: String) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                questionRepository.insertQuestionsReply(questionId, reply)
                getConversationData(questionId)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                //view.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                //view.snackbar(BindingUtils.string(R.string.no_internet_message))
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
            }

        }
    }

    fun getConversationData(questionId: Int) {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_reply_list))

                val response = questionRepository.getConversationData(questionId)
                questionsReplyListLiveData.postValue(response)

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK,
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )
                coroutineToggleLoader()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.reply_unavailable),
                    View.GONE
                )
                //questionsReplyListLiveData.postValue(arrayListOf())
                coroutineToggleLoader()
            }
        }

    }

    /*
    * Add Reply Page Ended ---
    * */

    fun alertMediaPlayBack(message:String,image:Drawable,btnName:String){
        setAlertDialogResourceModelMutableLiveData(
            message,
            image,
            true,
            btnName, {
                isAlertDialogShown.postValue(false)
            }/*,
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }*/
        )
        isAlertDialogShown.postValue(true)
    }

}