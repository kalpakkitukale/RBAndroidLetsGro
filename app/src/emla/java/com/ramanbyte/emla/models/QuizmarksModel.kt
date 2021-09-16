package com.ramanbyte.emla.models

import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
class QuizmarksModel {
    var quizmarks = TestSubmitModel()
    var quizsubmissionEntity = ArrayList<AnswerEntity>()
}