package com.ramanbyte.data_layer.room.base

import androidx.room.Insert
import androidx.room.Update

interface BaseDao<Entity> {

    @Insert
    fun insert(obj: Entity): Long

    @Update
    fun update(obj: Entity) : Int

    @Insert
    fun insert(obj: List<Entity>)

    @Update
    fun update(obj: List<Entity>)

    /*  @Insert
      fun insert(timeTablePagedList: LiveData<PagedList<TimeTableModel>>)*/
}