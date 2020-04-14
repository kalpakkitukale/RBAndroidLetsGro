package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.ramanbyte.data_layer.SharedPreferencesDatabase.getIntPref
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.room.entities.MediaInfoEntity
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.utilities.KEY_DEFAULT_MEDIA_STATUS
import com.ramanbyte.utilities.replicate

class ContentRepository(mContext: Context) : BaseRepository(mContext = mContext) {

    val userModel =
        applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

    var userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

    suspend fun getContent(sectionId: Int): ArrayList<ContentModel> {

        return apiRequest {

            idbiApiController.getContent(sectionId, userModel?.userId ?: 0)

        } ?: arrayListOf()

    }

    fun insertMediaInfo(mediaInfoModel: MediaInfoModel) {  //

        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

        mediaInfoModel.apply {
            this.userId = userId
        }

        applicationDatabase.apply {
            getMediaInfoDao().deleteMediaInfo(mediaInfoModel.mediaId, userId)
            getMediaInfoDao().insert(mediaInfoModel.replicate<MediaInfoModel, MediaInfoEntity>()!!)
        }
    }

    fun getMediaInfo(mediaId: Int): MediaInfoModel? {

        return applicationDatabase.getMediaInfoDao().getMediaInfo(mediaId)
            ?.replicate<MediaInfoEntity, MediaInfoModel>()
    }

    fun getMediaInfoLiveData(mediaId: Int): LiveData<MediaInfoEntity?> {  ///
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return applicationDatabase.getMediaInfoDao().getMediaInfoLiveData(mediaId, userId)
    }

    fun getMediaInfoByChapterId(chapterId: Int): LiveData<List<MediaInfoEntity>> {  ///
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return applicationDatabase?.getMediaInfoDao()?.getMediaInfoChapterId(chapterId, userId)
    }

    fun getMediaInfoBySectionId(sectionId: Int): LiveData<List<MediaInfoEntity>> {  ///
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return applicationDatabase?.getMediaInfoDao()?.getMediaInfoBySectionId(sectionId, userId)
    }

    fun updateMediaInfoByDownloadId(mediaStatus: Int, downloadId: String) {

        applicationDatabase.getMediaInfoDao().updateMediaStatusByDownloadId(mediaStatus, downloadId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) {

        applicationDatabase.apply {

            getMediaInfoDao().update(mediaInfoModel.replicate<MediaInfoModel, MediaInfoEntity>()!!)
        }
    }

    suspend fun syncMediaInfoToServer() {
        val dataToSync = applicationDatabase.getMediaInfoDao().getUnsynced()
    }

    fun getMediaInfoAll(): List<MediaInfoModel> {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return applicationDatabase.getMediaInfoDao()
            .getMediaInfoAll(KEY_DEFAULT_MEDIA_STATUS, userId).map {
                it.replicate<MediaInfoEntity, MediaInfoModel>()!!
            }
    }

    fun deleteMediaInfo(mediaId: Int): Int {
        val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!
        return applicationDatabase.getMediaInfoDao().deleteMediaInfo(mediaId, userId)
    }

    fun getCountMediaInfo(mediaId: Int): Int {
        return applicationDatabase.getMediaInfoDao().getCountMediaInfo(mediaId)
    }

    /*
    * call the api to insert SectionContentLog to the server
    * */
    suspend fun insertSectionContentLog(): Int {
        var result: Int? = 0
        /*
        * get all the record from local database which is not sync to server
        * */
        val allMediaInfoList =
            applicationDatabase.getMediaInfoDao().getMediaInfoListForSync(userId, 0)

        allMediaInfoList.forEach {
            it.createdBy = userId
            it.modifiedBy = userId
            it.device_Id = getIntPref(KEY_DEVICE_ID)
            result = apiRequest {
                idbiApiController.insertSectionContentLog(it)
            }!!
            if (result!! > 0) {
                /*
                * if data is sync to server then change the syncStatus = 1 into local table
                * */
                it.syncStatus = 1
                applicationDatabase.getMediaInfoDao().update(it)
            }
        }
        return result!!
    }
}