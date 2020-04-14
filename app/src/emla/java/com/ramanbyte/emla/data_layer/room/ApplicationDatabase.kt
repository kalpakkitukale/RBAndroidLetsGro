package com.ramanbyte.emla.data_layer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ramanbyte.emla.data_layer.room.dao.UserDao
import com.ramanbyte.emla.data_layer.room.data_converter.JsonConverter
import com.ramanbyte.emla.data_layer.room.entities.UserEntity

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */


@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ApplicationDatabase : RoomDatabase() {

    /**Room database DAOs*/
    abstract fun getUserDao(): UserDao

    companion object {

        /*
        * Instantiate Local Database for Application
        * */
        private var applicationDatabase: ApplicationDatabase? = null

        operator fun invoke(mContext: Context) = applicationDatabase
            ?: synchronized("") {

                applicationDatabase
                    ?: buildDatabase(
                        mContext
                    ).also {
                        applicationDatabase = it
                    }
            }

        private fun buildDatabase(mContext: Context) = Room.databaseBuilder(
            mContext, ApplicationDatabase::class.java, "db_emla.db"
        ).allowMainThreadQueries().build()
        /*
        * Initialization Complete
        * */
    }
}