package com.ramanbyte.emla.data_layer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ramanbyte.placement.data_layer.room.data_converter.JsonConverter

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */


@Database(
    entities = [LogInResponseModel::class] ,
    version = 1

)
@TypeConverters(JsonConverter::class)
abstract class ApplicationDatabase : RoomDatabase() {

    /**Room database DAOs*/

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