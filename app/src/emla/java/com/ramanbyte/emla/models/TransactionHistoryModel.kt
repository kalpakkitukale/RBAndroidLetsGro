package com.ramanbyte.emla.models

import com.ramanbyte.R
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.DateUtils.TIME_DISPLAY_PATTERN
import com.ramanbyte.utilities.KEY_BLANK

/**
 * Created by Mansi Manakiki on 11 Feb, 2021
 * Email : mansi.m@ramanbyte.com
 */

class TransactionHistoryModel {
    var id: Int = 0
    var userId: Int = 0
    var transId: String = ""
    var transDate: String = KEY_BLANK
        get() = DateUtils.getDisplayDateFromDate(
            field,
            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS,
            DateUtils.DATE_DISPLAY_PATTERN + " | " + TIME_DISPLAY_PATTERN
        )
    var amountPaid: Double = 0.0
    var paymentMethod: String = KEY_BLANK
    var transactionStatus: String? = ""
    var textColor = BindingUtils.color(R.color.black)
    var fees: List<CourseFeesModel> = arrayListOf()

}