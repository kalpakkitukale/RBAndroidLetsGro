package com.ramanbyte.emla.data_layer.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ramanbyte.emla.data_layer.room.base.BaseDao
import com.ramanbyte.emla.data_layer.room.entities.UserEntity

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

@Dao
abstract class UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM UserEntity LIMIT 1")
    abstract fun getCurrentUser(): UserEntity?

    @Query("DELETE FROM UserEntity")
    abstract fun delete()

    @Query("UPDATE UserEntity SET loggedId='Y'")
    abstract fun updateCurrentUserStatus()

}