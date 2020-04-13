package com.ramanbyte.emla.data_layer.room.base

import androidx.room.Insert
import androidx.room.Update

interface BaseDao<Entity> {
    @Insert
    fun insert(obj: Entity): Long

    @Update
    fun update(obj: Entity)

    @Insert
    fun insert(obj: List<Entity>)

    @Update
    fun update(obj: List<Entity>)
}