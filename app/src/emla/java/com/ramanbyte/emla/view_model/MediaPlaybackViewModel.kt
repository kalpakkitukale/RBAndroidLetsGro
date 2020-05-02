package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog
import org.kodein.di.generic.instance

class MediaPlaybackViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val contentRepository: ContentRepository by instance()
    private val sectionsRepository: SectionsRepository by instance()
    private val questionRepository: QuestionRepository by instance()

    var mediaInfoModel: MediaInfoModel? = null

    var onClickCloseCommentLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickAskQuestionLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = contentRepository.getMediaInfo(mediaId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) =
        contentRepository.updateMediaInfo(mediaInfoModel)

    override var noInternetTryAgain: () -> Unit = {}


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


    fun insertAskQuestion(question:String) {
        CoroutineUtils.main {
            try {
                //isLoaderShowingLiveData.postValue(true)
                //contentRepository.updateMediaInfo(mediaInfoModel)
                questionRepository.insertAskQuestion(mediaInfoModel!!, question)

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

    fun onClickCloseComment(view: View) {
        onClickCloseCommentLiveData.value = true
    }


    fun onClickAskQuestion(view: View) {
        onClickAskQuestionLiveData.value = true
    }

}