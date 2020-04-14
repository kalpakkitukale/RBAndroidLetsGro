package com.ramanbyte.emla.models

import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.DateUtils.DATE_DISPLAY_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_SERVER_PATTERN
import com.ramanbyte.utilities.DateUtils.TIME_DISPLAY_PATTERN
import com.ramanbyte.utilities.DateUtils.TIME_SERVER_PATTERN


/**
 * Created by Kunal Rathod
 * 28/12/19
 */
class CourseResultModel {
    var quizid: Int = 0
    var examdate: String = ""
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DATE_SERVER_PATTERN,
            DATE_DISPLAY_PATTERN
        )

    var day: String? = null
    var obtainedmarks: Double = 0.0
    var totalmarks: Double = 0.0
    var passPercentage: Float = 0f
    var obtainedPercentage: Float = 0f
    var starttime: String? = null
    var endtime: String? = null
    var totminutes: String = ""
        get() = DateUtils.getDisplayDateFromDate(
            field,
            TIME_SERVER_PATTERN,
            TIME_DISPLAY_PATTERN
        )

    var index : String = ""
}