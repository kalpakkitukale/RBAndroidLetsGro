package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

class ContentViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val contentRepository: ContentRepository by instance()

    val playOrPreviewLiveData = MutableLiveData<ContentModel?>(null)
    val downloadLiveData = MutableLiveData<ContentModel?>(null)
    val deleteMediaLiveData = MutableLiveData<MediaInfoModel?>(null)

    var courseId: Int? = 0
    var courseName: String? = KEY_BLANK
    var chapterId = 0
    var chapterName: String? = KEY_BLANK
    var sectionId = 0
    var sectionName: String? = ""

    override var noInternetTryAgain: () -> Unit = {
        getContentList()
    }

    val contentListLiveData = MutableLiveData<ArrayList<ContentModel>>()

    fun getContentList() {

        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_content_details))

                val contentList = contentRepository.getContentList(sectionId)

                contentListLiveData.postValue(contentList)

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
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.no_content_uploaded),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }

    }

    fun playOrShowContent(contentModel: ContentModel) {
        playOrPreviewLiveData.value = contentModel
    }

    fun downloadContent(contentModel: ContentModel) {
        downloadLiveData.value = contentModel
    }

    fun addMediaInfo(mediaInfoModel: MediaInfoModel): Long =
        contentRepository.insertMediaInfo(mediaInfoModel)

    fun getMediaInfoModel(mediaId: Int): MediaInfoModel? =
        contentRepository.getMediaInfo(mediaId)

    fun getMediaInfoModelLiveData(mediaId: Int): LiveData<MediaInfoModel?> =
        contentRepository.getMediaInfoModelLiveData(mediaId)

    fun isMediaDownloaded(contentModel: ContentModel): Boolean =
        contentRepository.isMediaDownloaded(contentModel, deleteMediaLiveData)

    fun deleteMedia(mediaId: Int) = contentRepository.deleteMediaInfo(mediaId)
}