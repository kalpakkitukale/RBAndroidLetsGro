package com.ramanbyte.emla.data_layer.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.emla.data_layer.room.base.BaseDao
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity


@Dao
abstract class AnswerDao : BaseDao<AnswerEntity> {

    @Query("UPDATE AnswerEntity SET question_Id = :question_Id, options = :options, iscorrect = :iscorrect WHERE question_Id = :question_Id ")
    abstract fun updateOption(
        options: String,
        iscorrect: String,
        question_Id: Int
    )

    @Query("SELECT COUNT(*) FROM AnswerEntity WHERE question_Id = :question_Id")
    abstract fun getQuestionRelatedOptionCount(question_Id: Int): Int

    @Query("DELETE FROM AnswerEntity WHERE question_Id = :question_Id")
    abstract fun deleteQuestionRelatedOption(question_Id: Int)

    @Query("SELECT * FROM AnswerEntity")
    abstract fun getAllQuestionRelatedOption(): List<AnswerEntity>

    @Query("SELECT * FROM AnswerEntity WHERE question_Id = :question_Id")
    abstract fun getAnswerForQuestion(question_Id: Int): AnswerEntity?

    @Query("SELECT COUNT(*) FROM AnswerEntity WHERE question_Id = :question_Id AND answer <> 0")
    abstract fun isQuestionAttempted(question_Id: Int): Int?

    @Query("DELETE FROM AnswerEntity")
    abstract fun deleteAll()

    @Query("SELECT SUM(total_Marks) FROM AnswerEntity WHERE quiz_Id = :quizId and iscorrect = :iscorrect")
    abstract fun getTotalObtainedMarks(quizId: Int, iscorrect: String): Float

}