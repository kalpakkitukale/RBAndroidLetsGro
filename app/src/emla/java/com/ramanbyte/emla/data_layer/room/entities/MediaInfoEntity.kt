package com.ramanbyte.emla.data_layer.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ramanbyte.utilities.*

@Entity
class MediaInfoEntity {

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0

    @SerializedName("user_Id")
    var userId: Int = 0

    @SerializedName("content_Id")
    var mediaId: Int = 0

    var mediaUrl: String = ""

    var duration: Long = 0

    var seekPosition: Long = 0

    @SerializedName("status")
    var mediaStatus: Int = -1

    @SerializedName("content_Type")
    var mediaType: String = ""

    var expirationDate: String = ""

    /*
    * syncStatus = 1 then sync
    * syncStatus = 0 then not sync
    * */
    var syncStatus: Int = 0

    var sectionId: Int = 0

    var chapterId: Int = 0

    var requestId: String = ""

    /*
    * Added Extra Column
    * */
    var courseId: Int = 0
    var courseName: String = KEY_BLANK
    var chapterName: String = KEY_BLANK
    var sectionName: String = KEY_BLANK
    var contentLink: String = KEY_BLANK


    /*
    * this all fields for the InsertSectionContentLog API
    * */
    @Ignore
    var id: Int = 0
    @Ignore
    var device_Id: Int? = 0
    @Ignore
    var createdBy: Int? = 0
    @Ignore
    var modifiedBy: Int? = 0
    @Ignore
    var createdDate: String? = DateUtils.getCurDate()
    @Ignore
    var modifiedDate: String? = DateUtils.getCurDate()
    @Ignore
    var app_Status: String? = KEY_APP
    @Ignore
    var del_Status: String? = KEY_N
    @Ignore
    var ipAddress: String? = IpUtility.getIpAddress()

}