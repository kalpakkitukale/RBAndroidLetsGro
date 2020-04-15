package com.ramanbyte.emla.models

import com.ramanbyte.utilities.KEY_BLANK

class QuizReviewModel {
    var questionId = 0
    var questionTitle = KEY_BLANK
    var options= ArrayList<QuizReviewOptionModel>()
    var isCorrect = KEY_BLANK
    var ansId = 0
    var testDate = KEY_BLANK
    var testTitle = KEY_BLANK
}