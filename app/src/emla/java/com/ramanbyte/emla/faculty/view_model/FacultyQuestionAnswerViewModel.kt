package com.ramanbyte.emla.faculty.view_model

import android.content.ClipData
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

class FacultyQuestionAnswerViewModel(val mContext: Context) : BaseViewModel(mContext = mContext) {

    private val questionRepository: QuestionRepository by instance()

    var questionsReplyListLiveData = MutableLiveData<ArrayList<AskQuestionReplyModel>>().apply {
        value = arrayListOf()
    }

    var enteredReplyLiveData = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }
    var onClickAddReplyLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var onClickMenuLiveData = MutableLiveData<AskQuestionReplyModel>().apply {
        value = null
    }
    var onClickCloseBottomSheetLiveData = MutableLiveData<Boolean>().apply {
        value = null
    }
    var visibilityReplyBtnLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    var facultyReplyVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    var questionId = 0
    var reply = KEY_BLANK
    var replyId = 0
    var isEdit = false

    override var noInternetTryAgain: () -> Unit = {
        getConversationData()
    }

    fun getConversationData() {
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
                /*toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.reply_unavailable),
                    View.GONE
                )*/
                coroutineToggleLoader()
            }
        }

    }


    fun insertQuestionsReply(reply: String) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)

                if (isEdit)
                    questionRepository.insertQuestionsReply(questionId, reply, replyId, KEY_EDITED)
                else
                    questionRepository.insertQuestionsReply(questionId, reply, 0, KEY_NOT_EDITED)

                enteredReplyLiveData.postValue(KEY_BLANK)
                isEdit = false
                getConversationData()
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

    fun onClickAddReply(view: View) {
        onClickAddReplyLiveData.value = true
    }

    fun onClickMenu(replyModel: AskQuestionReplyModel) {
        onClickMenuLiveData.value = replyModel
    }

    fun onClickCloseBottomSheet(view: View) {
        onClickCloseBottomSheetLiveData.value = true
    }

    /*
    * Edit the selected reply
    * */
    fun onClickEdit(view: View) {
        enteredReplyLiveData.value = reply
        isEdit = true
        onClickCloseBottomSheet(view)
    }

    /*
    * Copy the selected reply into the clipboard
    * */
    fun onClickCopy(view: View) {

        val clipboard =
            mContext.getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip =
            ClipData.newPlainText(BindingUtils.string(R.string.copied_reply), reply)
        clipboard.setPrimaryClip(clip)

        isEdit = false
        onClickCloseBottomSheet(view)
    }

    /*
    * To delete the selected reply (for now there no functionality)
    * */
    fun onClickDelete(view: View) {
        isEdit = false
        AppLog.infoLog("onClickDelete")
    }
}