package com.ramanbyte.emla.data_layer.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.emla.data_layer.room.base.BaseDao
import com.ramanbyte.emla.data_layer.room.entities.QuestionAndAnswerEntity

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 20/12/19
 */

@Dao
abstract class QuestionAndAnswerDao : BaseDao<QuestionAndAnswerEntity> {

    @Query("SELECT * FROM QuestionAndAnswerEntity ")
    abstract fun getAllQuestion(): List<QuestionAndAnswerEntity?>

    @Query("SELECT * FROM QuestionAndAnswerEntity LIMIT 1")
    abstract fun submitAllQuestion(): QuestionAndAnswerEntity?

    @Query("DELETE FROM QuestionAndAnswerEntity")
    abstract fun deleteAllQuestion()

    @Query("SELECT SUM(marks) FROM QuestionAndAnswerEntity WHERE quizid  = :quizId")
    abstract fun getTotalMarks(quizId: Int): Float

    @Query("SELECT passignpercentage FROM QuestionAndAnswerEntity WHERE quizid  = :quizId")
    abstract fun getPassingPercent(quizId: Int): Float

    @Query("SELECT COUNT(*) FROM QuestionAndAnswerEntity")
    abstract fun getTotalQuestionCount(): Int?
}