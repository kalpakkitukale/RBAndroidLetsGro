package com.ramanbyte.emla.models

import android.view.View
import androidx.databinding.Bindable

class SectionsModel {
    var id: Int = 0
    var sequenceNo: Int = 0
    var course_Id: Int = 0
    var chapter_Id: Int = 0
    var section_Name: String = ""
    var description: String = ""
    var createdBy: Int = 0
    var modifiedBy: Int = 0
    var createdDate: String = ""
    var modifiedDate: String = ""
    var app_Status: String = ""
    var del_Status: String = ""
    var ipAddress: String = ""
    var sequenceStatus: String = ""
    var sequenceId: Int = 0
    var before: Int = 0
    var after: Int = 0
    var contentCount: Int = 0
    var contentDocumentCount: Int = 0

    @Bindable
    var downloadVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.downloadVisibility)
        }
}