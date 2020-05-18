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
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.NetworkConnectivity
import kotlinx.coroutines.delay
import org.kodein.di.generic.instance

class MyFavouriteVideoViewModel(var mContext: Context) : BaseViewModel(mContext) {

    private val questionRepository: QuestionRepository by instance()
    private val sectionsRepository: SectionsRepository by instance()

    val playOrPreviewLiveData = MutableLiveData<MediaInfoModel>(null)

    var favouriteVideosListLiveData = MutableLiveData<List<FavouriteVideosModel>>().apply {
        value = arrayListOf()
    }

    var mediaInfoModel: MediaInfoModel? = null

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = questionRepository.getMediaInfo(mediaId)
    }

    override var noInternetTryAgain: () -> Unit = {
        getFavouriteVideos()
    }

    fun getFavouriteVideos() {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_my_favourite_video_list))

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

    fun insertFavouriteVideoStatus(
        favouriteVideosModel: FavouriteVideosModel
    ) {
        CoroutineUtils.main {

            try {
                isLoaderShowingLiveData.postValue(true)
                val response = questionRepository.insertFavouriteVideoStatus(favouriteVideosModel)
                getFavouriteVideos()
                //insertedFavouriteStatus.postValue(response)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.ic_something_went_wrong)!!,
                    true,
                    BindingUtils.string(R.string.strOk), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    false,
                    BindingUtils.string(R.string.try_again), {
                        isAlertDialogShown.postValue(false)
                        insertFavouriteVideoStatus(favouriteVideosModel)
                    },
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                delay(200)
                isAlertDialogShown.postValue(true)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
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

    fun playMedia(favouriteVideosModel: FavouriteVideosModel) {
        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
            playOrPreviewLiveData.value = MediaInfoModel().apply {
                mediaId = favouriteVideosModel.contentId
                contentLink = favouriteVideosModel.videoLink
            }
        } else {
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.no_internet_message),
                BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                false,
                BindingUtils.string(R.string.try_again), {
                    playMedia(favouriteVideosModel)
                    isAlertDialogShown.postValue(false)
                },
                negativeButtonClickFunctionality = {
                    isAlertDialogShown.postValue(false)
                }
            )
            isAlertDialogShown.postValue(true)
        }

        //playOrPreviewLiveData.value = mediaInfoModel
    }

    fun removeFavourite(view: View, favouriteVideosModel: FavouriteVideosModel) {

        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.message_remove_favourite),
            null,
            false,
            positiveButtonText = BindingUtils.string(R.string.strYes),
            positiveButtonClickFunctionality = {
                insertFavouriteVideoStatus(favouriteVideosModel.apply {
                    isFavouriteVideo = KEY_BLANK
                })

                isAlertDialogShown.value = false
            },
            negativeButtonText = BindingUtils.string(R.string.strNo),
            negativeButtonClickFunctionality = {
                isAlertDialogShown.value = false
            })

        isAlertDialogShown.value = true
    }

}