package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR
import com.ramanbyte.R
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_NA_WITHOUT_SPACE

class ContentModel() : Parcelable, BaseObservable() {

    var id: Int = 0

    var chapter_Id = 0
    var content_link = ""
    var content_Type = ""
    var description = ""

    var isVideo = false

    var duration: Long = 0
    var completedPosition: Int = 0 //seekPosition

    var sectionId = 0
    var chapterName = KEY_BLANK
    var sectionName = KEY_BLANK

    /*
    * this parameter added By Niraj
    * */
    var isLike: String = KEY_BLANK
    var isFavourite: String = KEY_BLANK
    var contentTitle: String? = KEY_BLANK
        get() = field ?: KEY_NA_WITHOUT_SPACE

    @Bindable
    var downloadVisibility: Int = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.downloadVisibility)
        }

    /*
    * Status
    * */
    @Bindable
    var downlaodStatus = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.downlaodStatus)
        }

    @Bindable
    var downloadStatusVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.downloadStatusVisibility)
        }

    @Bindable
    var downloadStatusColor = BindingUtils.color(R.color.colorIconGreen)
        set(value) {
            field = value
            notifyPropertyChanged(BR.downloadStatusColor)
        }

    @Bindable
    var isDownloaded = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.isDownloaded)
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(chapter_Id)
        parcel.writeString(content_link)
        parcel.writeString(content_Type)
        parcel.writeString(description)
        parcel.writeByte(if (isVideo) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentModel> {
        override fun createFromParcel(parcel: Parcel): ContentModel {
            return ContentModel().apply {
                id = parcel.readInt()
                chapter_Id = parcel.readInt()
                content_link = parcel.readString() ?: ""
                content_Type = parcel.readString() ?: ""
                description = parcel.readString() ?: ""
                isVideo = parcel.readByte() != 0.toByte()
            }
        }

        override fun newArray(size: Int): Array<ContentModel?> {
            return arrayOfNulls(size)
        }
    }
}