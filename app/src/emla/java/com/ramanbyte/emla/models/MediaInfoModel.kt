package com.ramanbyte.emla.models

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ramanbyte.BR
import com.ramanbyte.utilities.KEY_BLANK

@Entity
class MediaInfoModel : BaseObservable() {

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0

    var userId: Int = 0

    var mediaId: Int = 0

    var mediaUrl: String = ""

    var duration: Long = 0

    var seekPosition: Long = 0

    var mediaStatus: Int = -1

    var mediaType: String = ""

    var expirationDate: String = ""

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
}