package com.ramanbyte.emla.data_layer.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.data_layer.room.base.BaseDao
import com.ramanbyte.emla.models.MediaInfoModel

@Dao
interface MediaInfoDao : BaseDao<MediaInfoModel> {

    @Query("SELECT * FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun getMediaInfo(mediaId: Int): MediaInfoModel?

    @Query("SELECT * FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun getMediaInfoLiveData(mediaId: Int): LiveData<MediaInfoModel?>

    @Query("SELECT * FROM MediaInfoModel WHERE chapterId = :topicId")
    fun getMediaInfoChapterId(topicId: Int): LiveData<List<MediaInfoModel>>

    @Query("SELECT * FROM MediaInfoModel WHERE sectionId = :divisionId")
    fun getMediaInfoBySectionId(divisionId: Int): LiveData<List<MediaInfoModel>>

    @Query("SELECT * FROM MediaInfoModel WHERE syncStatus = 0")
    fun getUnsynced(): List<MediaInfoModel>

    @Query("UPDATE MediaInfoModel SET mediaStatus = :mediaStatus WHERE requestId = :downloadId")
    fun updateMediaStatusByDownloadId(mediaStatus: Int, downloadId: String)

    @Query("SELECT * FROM MediaInfoModel WHERE mediaStatus <>:mediaStatus")  // WHERE mediaStatus = 3
    fun getMediaInfoAll(mediaStatus: Int): List<MediaInfoModel>

    @Query("SELECT COUNT(*) FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun getCountMediaInfo(mediaId: Int): Int

    @Query("DELETE FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun deleteMediaInfo(mediaId: Int): Int
}