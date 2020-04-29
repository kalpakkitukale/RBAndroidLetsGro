package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class MediaPlaybackViewModel(mContext: Context) : BaseViewModel(mContext) {

    //Repo
    private val contentRepository: ContentRepository by instance()
    private val sectionsRepository: SectionsRepository by instance()

    var mediaInfoModel: MediaInfoModel? = null

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = contentRepository.getMediaInfo(mediaId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) =
        contentRepository.updateMediaInfo(mediaInfoModel)

    override var noInternetTryAgain: () -> Unit = {}


    fun insertSectionContentLog(
        whichClick: String,
        isLikeVideo: String,
        isFavouriteVideo: String,
        mediaId: Int
    ) {
        CoroutineUtils.main {

            try {
                //courseResultModelListLiveData.postValue(
                sectionsRepository.insertSectionContentLog(
                    whichClick,
                    isLikeVideo,
                    isFavouriteVideo,
                    mediaId
                )
                //)

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    "",
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
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }

        }
    }

}