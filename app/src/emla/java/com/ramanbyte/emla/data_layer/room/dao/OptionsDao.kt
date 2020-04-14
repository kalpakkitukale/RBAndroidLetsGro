package com.ramanbyte.emla.data_layer.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.emla.data_layer.room.base.BaseDao
import com.ramanbyte.emla.data_layer.room.entities.OptionsEntity

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 20/12/19
 */
@Dao
interface OptionsDao : BaseDao<OptionsEntity> {

    /*@Query("SELECT OE.localId, OE.questionsLocalId, OE.createdDate, OE.modifiedDate, OE.app_Status, OE.del_Status, OE.ipAddress, OE.id, OE.question_Id, OE.options, OE.iscorrect, AE.answer FROM OptionsEntity as OE LEFT OUTER JOIN AnswerEntity AE where OE.question_Id = :questionId AND (AE.question_Id=:questionId OR 1)")
    fun getQuestionRelatedOptions(questionId: Int): List<OptionsEntity?>*/

    @Query("SELECT * FROM OptionsEntity WHERE question_Id = :questionId ORDER BY localId")
    fun getQuestionRelatedOptions(questionId: Int): List<OptionsEntity?>

    @Query("DELETE FROM OptionsEntity")
    fun deleteAll()
}