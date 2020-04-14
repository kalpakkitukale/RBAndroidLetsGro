package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.idbi.utilities.KEY_BLANK
import com.ramanbyte.idbi.BR

class MediaInfoModel : BaseObservable(){

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

    @Bindable
    var isSelected: Boolean = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.isSelected)
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