package com.ramanbyte.emla.models

import com.ramanbyte.utilities.KEY_BLANK


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class InstructionsModel {

    var id: Int = 0
    var quiz_Title: String = KEY_BLANK
    var total_Marks: Float = 0.0f
    var quiz_Type: Int = 0
    var common_Instruction: String = KEY_BLANK
    var static_Instructions: String = KEY_BLANK
    var total_Questions: String = KEY_BLANK

}