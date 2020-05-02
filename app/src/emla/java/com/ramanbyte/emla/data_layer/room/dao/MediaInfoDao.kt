package com.ramanbyte.emla.data_layer.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.data_layer.room.base.BaseDao
import com.ramanbyte.emla.models.MediaInfoModel

@Dao
interface MediaInfoDao : BaseDao<MediaInfoModel> {

    @Query("SELECT * FROM MediaInfoModel WHERE mediaId = :mediaId AND userId = :userId")
    fun getMediaInfo(mediaId: Int, userId: Int): MediaInfoModel?

    @Query("SELECT * FROM MediaInfoModel WHERE mediaId = :mediaId AND userId = :userId")
    fun getMediaInfoLiveData(mediaId: Int, userId: Int): LiveData<MediaInfoModel?>

    @Query("SELECT * FROM MediaInfoModel WHERE chapterId = :topicId AND userId = :userId")
    fun getMediaInfoChapterId(topicId: Int, userId: Int): LiveData<List<MediaInfoModel>>

    @Query("SELECT * FROM MediaInfoModel WHERE sectionId = :divisionId AND userId = :userId")
    fun getMediaInfoBySectionId(divisionId: Int, userId: Int): LiveData<List<MediaInfoModel>>

    @Query("SELECT * FROM MediaInfoModel WHERE syncStatus = 0")
    fun getUnsynced(): List<MediaInfoModel>

    @Query("UPDATE MediaInfoModel SET mediaStatus = :mediaStatus WHERE requestId = :downloadId")
    fun updateMediaStatusByDownloadId(mediaStatus: Int, downloadId: String)

    @Query("SELECT * FROM MediaInfoModel WHERE mediaStatus <>:mediaStatus AND userId = :userId")  // WHERE mediaStatus = 3
    fun getMediaInfoAll(mediaStatus: Int, userId: Int): List<MediaInfoModel>

    @Query("SELECT COUNT(*) FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun getCountMediaInfo(mediaId: Int): Int

    @Query("DELETE FROM MediaInfoModel WHERE mediaId = :mediaId")
    fun deleteMediaInfo(mediaId: Int): Int

    @Query("UPDATE MediaInfoModel SET likeVideo = :likeVideo WHERE mediaId = :mediaId AND userId = :userId")
    fun updateMediaLikeVideoByMediaId(likeVideo: String, mediaId: Int, userId: Int)

    @Query("UPDATE MediaInfoModel SET favouriteVideo = :favouriteVideo WHERE mediaId = :mediaId AND userId = :userId")
    fun updateMediaFavouriteVideoByMediaId(favouriteVideo: String, mediaId: Int, userId: Int)
}