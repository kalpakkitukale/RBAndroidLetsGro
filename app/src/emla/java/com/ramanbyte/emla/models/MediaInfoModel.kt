package com.ramanbyte.emla.models

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ramanbyte.BR
import com.ramanbyte.utilities.*

@Entity
class MediaInfoModel : BaseObservable() {

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0

    @SerializedName("user_Id")
    var userId: Int = 0

    @SerializedName("content_Id")
    var mediaId: Int = 0

    var mediaUrl: String = ""

    var duration: Long = 0

    var seekPosition: String = "0"

    @SerializedName("status")
    var mediaStatus: String = "-1"

    @SerializedName("content_Type")
    var mediaType: String = ""

    var expirationDate: String = ""

    @SerializedName("section_Id")
    var sectionId: Int = 0

    @SerializedName("chapter_Id")
    var chapterId: Int = 0

    var requestId: String = ""

    /*
    * Added Extra Column
    * */
    @SerializedName("course_Id")
    var courseId: Int = 0
    var courseName: String = KEY_BLANK
    var chapterName: String = KEY_BLANK
    var sectionName: String = KEY_BLANK
    var contentLink: String = KEY_BLANK

    @SerializedName("isLikeVideo")
    var likeVideo: String = KEY_BLANK

    @SerializedName("isFavouriteVideo")
    var favouriteVideo: String = KEY_BLANK

    var contentTitle: String = KEY_BLANK

    var mimeType = ""

    @Bindable
    var isSelected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.isSelected)
        }

    var syncStatus = 0

    @Ignore
    @Bindable
    var previewButtonVisibility: Int = View.VISIBLE
        set(value) {
            field = value
            notifyPropertyChanged(BR.previewButtonVisibility)
        }

    @Ignore
    @Bindable
    var deleteButtonVisibility: Int = View.VISIBLE
        set(value) {
            field = value
            notifyPropertyChanged(BR.deleteButtonVisibility)
        }

    @Ignore
    @Bindable
    var noFileMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.noFileMessageVisibility)
        }

    /*var allowDenyVisibility = false
        @Bindable
        get() = field
        @Bindable
        set(value) {
            field = value
            notifyPropertyChanged(BR.allowDenyVisibility)
        }*/

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