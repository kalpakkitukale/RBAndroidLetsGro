package com.ramanbyte.emla.models

import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
class TestSubmitModel {

    var id: Int = 0
    var course_Id: Int = 0
    var chapter_Id: Int = 0
    var start_Time: String? = KEY_BLANK
    var end_Time: String? = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var quiz_Type: Int = 0
    var quiz_Id: Int = 0
    var employee_Id = 0
    var total_Marks = 0f
    var passing_Percent: Double = 0.0

    var modifiedBy = 0
    var modifiedDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var createdDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
    var ipAddress = IpUtility.getIpAddress()
    var del_Status = KEY_N
    var app_Status = KEY_APP
    var createdBy = 0
    var ischaptercompleted: String? = KEY_BLANK
    var iscoursecompleted: String? = KEY_BLANK
    var quizpassingpercent = 0

    var quizsubmissionEntity = ArrayList<AnswerEntity>()

}