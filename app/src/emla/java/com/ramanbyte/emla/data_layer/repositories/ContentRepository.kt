package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.offline.Download
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.SectionsController
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.FileUtils
import com.ramanbyte.utilities.KEY_MEDIA_TYPE_AUDIO
import com.ramanbyte.utilities.KEY_MEDIA_TYPE_VIDEO
import org.kodein.di.generic.instance
import java.io.File

class ContentRepository(mContext: Context) : BaseRepository(mContext) {

    val sectionsController: SectionsController by instance()

    suspend fun getContentList(sectionId: Int): ArrayList<ContentModel> {

        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0

        return apiRequest {
            sectionsController.getContentList(sectionId, userId)
        } ?: arrayListOf()
    }

    /*
    * Content Downloads
    * */

    fun insertMediaInfo(mediaInfoModel: MediaInfoModel): Long {

        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        mediaInfoModel.userId = userId

        applicationDatabase.getMediaInfoDao().apply {

            val mediaInfo = getMediaInfo(mediaInfoModel.mediaId)

            if (mediaInfo != null)
                return update(mediaInfo.apply {

                    mediaId = mediaInfoModel.mediaId
                    mediaUrl = mediaInfoModel.mediaUrl
                    duration = mediaInfoModel.duration
                    mediaType = mediaInfoModel.mediaType
                    expirationDate = mediaInfoModel.expirationDate
                    contentLink = mediaInfoModel.contentLink
                    requestId = mediaInfoModel.requestId
                    likeVideo = mediaInfoModel.likeVideo
                    favouriteVideo = mediaInfoModel.favouriteVideo
                    /*  createdDateTime = mediaInfoModel.createdDateTime
                      modifiedDateTime = mediaInfoModel.modifiedDateTime*/
                }).toLong()
            else
                return insert(mediaInfoModel)

        }
    }

    fun getMediaInfo(mediaId: Int): MediaInfoModel? {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase.getMediaInfoDao().getMediaInfo(mediaId, userId)
    }

    fun getMediaInfoModelLiveData(mediaId: Int): LiveData<MediaInfoModel?> {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase.getMediaInfoDao().getMediaInfoLiveData(mediaId, userId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) {
        applicationDatabase.getMediaInfoDao().update(mediaInfoModel)
    }

    fun updateMediaInfoByDownloadId(mediaStatus: Int, downloadId: String) {

        applicationDatabase.getMediaInfoDao().updateMediaStatusByDownloadId(mediaStatus, downloadId)

    }

    fun getMedias(): List<MediaInfoModel> {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase.getMediaInfoDao().getMediaInfoAll(-1, userId)
    }

    fun deleteMediaInfo(mediaId: Int): Int {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase.getMediaInfoDao().deleteMediaInfo(mediaId)
    }

    fun getMediaInfoByChapterId(chapterId: Int): LiveData<List<MediaInfoModel>> {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase?.getMediaInfoDao()?.getMediaInfoChapterId(chapterId, userId)
    }

    fun getMediaInfoBySectionId(sectionId: Int): LiveData<List<MediaInfoModel>> {
        val userId = applicationDatabase.getUserDao()?.getCurrentUser()?.userId ?: 0
        return applicationDatabase?.getMediaInfoDao()?.getMediaInfoBySectionId(sectionId, userId)
    }

    fun isMediaDownloaded(
        contentModel: ContentModel,
        deleteMediaLiveData: MutableLiveData<MediaInfoModel?>
    ): Boolean {

        var isMediaDownloaded = false

        val mediaInfoModel = getMediaInfo(contentModel.id)
            ?: return false

/*        val modifiedDate = DateUtils.getCalendarByCustomDate(
            contentModel.modifiedDateTime ?: "",
            DateUtils.DATE_WEB_API_FORMAT_2
        )
        val presentModifiedDate = DateUtils.getCalendarByCustomDate(
            mediaInfoModel?.modifiedDateTime ?: "",
            DateUtils.DATE_WEB_API_FORMAT_2
        )

        val isNewRecord = modifiedDate?.after(presentModifiedDate) ?: false

        if (isNewRecord) {
            isMediaDownloaded = false

            if (mediaInfoModel.mediaStatus == Download.STATE_COMPLETED)
                deleteMediaLiveData.value = mediaInfoModel
            else
                deleteMediaInfo(mediaInfoModel.mediaId)
        } else {
        }*/

        isMediaDownloaded = mediaInfoModel?.mediaStatus.toInt() == Download.STATE_COMPLETED

        mediaInfoModel.apply {
            mimeType = FileUtils.getMimeType(contentLink) ?: ""

            if (!(mimeType?.contains(KEY_MEDIA_TYPE_VIDEO) || mimeType?.contains(
                    KEY_MEDIA_TYPE_AUDIO
                ) && isMediaDownloaded)
            )
                isMediaDownloaded =
                    isMediaDownloaded && mediaUrl.isNotEmpty() && File(mediaUrl).exists()

        }

        return isMediaDownloaded
    }

    /*
    * Content Downloaded Offline
    * */
}