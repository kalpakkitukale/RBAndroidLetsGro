package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

class MyFavouriteVideoViewModel(mContext:Context) : BaseViewModel(mContext) {

    private val questionRepository: QuestionRepository by instance()
    private val sectionsRepository: SectionsRepository by instance()

    var favouriteVideosListLiveData = MutableLiveData<List<FavouriteVideosModel>>().apply {
        value = arrayListOf()
    }

    var onClickFavouriteVideosLiveData = MutableLiveData<Int>().apply {
        value = 0
    }

    var insertedFavouriteStatus = MutableLiveData<Int>().apply {
        value = 0
    }

    var mediaInfoModel: MediaInfoModel? = null

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = questionRepository.getMediaInfo(mediaId)
    }

    override var noInternetTryAgain: () -> Unit = {
        getFavouriteVideos()
    }

    fun getFavouriteVideos(){
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_questions_list))

                val response = questionRepository.getFavouriteVideos()
                favouriteVideosListLiveData.postValue(response)

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
                    BindingUtils.string(R.string.favourite_videos_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }

    }

    fun insertSectionContentLog(
        mediaId: Int,
        mediaInfoModel: MediaInfoModel
    ) {
        CoroutineUtils.main {

            try {
                //isLoaderShowingLiveData.postValue(true)
                questionRepository.updateMediaInfo(mediaInfoModel)
                val response = questionRepository.insertSectionContentLog(mediaId)
                getFavouriteVideos()
                //insertedFavouriteStatus.postValue(response)
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

    fun showNoInternetDialog(message: String = BindingUtils.string(R.string.no_internet_message)) {

        setAlertDialogResourceModelMutableLiveData(
            message,
            BindingUtils.drawable(R.drawable.ic_no_internet),
            true,
            positiveButtonText = BindingUtils.string(R.string.strOk),
            positiveButtonClickFunctionality = {
                isAlertDialogShown.value = false
            })

        isAlertDialogShown.value = true
    }

}