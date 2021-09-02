package com.ramanbyte.emla.models

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ramanbyte.BR

class ChaptersModel() : Parcelable, BaseObservable() {

    var chapterId: Int? = 0
    var chapterName: String? = ""
    var description: String? = ""
    var sequenceNo: Int? = 0
    var sequencestatus: String? = null
    var sequenceId: Int = 0
    var sectionListDropdown: String? = ""
    var sectionlist = ArrayList<SectionsModel>()
    var totalSectionCount: Int = 0
    var formativeAssessmentStaus: Boolean? = false

    @Bindable
    var downloadVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.downloadVisibility)
        }

    var index: String? = ""

    constructor(parcel: Parcel) : this() {
        chapterId = parcel.readInt()
        chapterName = parcel.readString()
        description = parcel.readString()
        index = parcel.readString()
        totalSectionCount = parcel.readInt()
        formativeAssessmentStaus = parcel.readValue(Int::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeInt(chapterId!!)
        parcel?.writeString(chapterName)
        parcel?.writeString(description)
        parcel?.writeString(index)
        parcel?.writeInt(totalSectionCount)
        parcel?.writeValue(formativeAssessmentStaus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChaptersModel> {
        override fun createFromParcel(parcel: Parcel): ChaptersModel {
            return ChaptersModel(parcel)
        }

        override fun newArray(size: Int): Array<ChaptersModel?> {
            return arrayOfNulls(size)
        }
    }
}