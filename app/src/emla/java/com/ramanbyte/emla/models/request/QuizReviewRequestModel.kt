package com.ramanbyte.emla.models.request

import com.ramanbyte.emla.models.BaseModel
import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.KEY_BLANK

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
class QuizReviewRequestModel : BaseRequestModel() {
    var EmpId = 0
    var quizId = 0
    var status = KEY_BLANK
    var attemptStatus = 0
}