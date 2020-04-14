package com.ramanbyte.emla.data_layer.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

@Entity
class AnswerEntity {
    @PrimaryKey(autoGenerate = true)
    var localId = 0

    var question_Id: Int = 0
    var options: String = KEY_BLANK
    var iscorrect: String = KEY_BLANK

    @Ignore
    var id = 0
    var quiz_Id = 0
    var employee_Id = 0
    var answer = 0
    var total_Marks : Double = 0.0
    @Ignore
    var createdBy = 0
    @Ignore
    var modifiedBy = 0
    @Ignore
    var createdDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    @Ignore
    var modifiedDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    @Ignore
    var app_Status = KEY_APP
    @Ignore
    var del_Status = KEY_N
    @Ignore
    var ipAddress = IpUtility.getIpAddress()
}