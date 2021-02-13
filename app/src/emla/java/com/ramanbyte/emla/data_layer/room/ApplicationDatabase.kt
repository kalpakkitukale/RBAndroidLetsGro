package com.ramanbyte.emla.data_layer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ramanbyte.emla.data_layer.room.dao.*
import com.ramanbyte.emla.data_layer.room.data_converter.JsonConverter
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.data_layer.room.entities.OptionsEntity
import com.ramanbyte.emla.data_layer.room.entities.QuestionAndAnswerEntity
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.AppLog

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */


@Database(
    entities = [UserEntity::class, QuestionAndAnswerEntity::class, OptionsEntity::class, AnswerEntity::class, MediaInfoModel::class],
    version = 3,
    exportSchema = true
)
abstract class ApplicationDatabase : RoomDatabase() {

    /**Room database DAOs*/
    abstract fun getUserDao(): UserDao
    abstract fun getQuestionAndAnswerDao(): QuestionAndAnswerDao
    abstract fun getOptionsDao(): OptionsDao
    abstract fun getAnswerDao(): AnswerDao
    abstract fun getMediaInfoDao(): MediaInfoDao

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
        ).allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build()
        /*
        * Initialization Complete
        * */

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE MediaInfoModel ADD likeVideo VARCHAR DEFAULT '' NOT NULL")
                database.execSQL("ALTER TABLE MediaInfoModel ADD favouriteVideo VARCHAR DEFAULT '' NOT NULL")
                database.execSQL("ALTER TABLE MediaInfoModel ADD contentTitle VARCHAR DEFAULT '' NOT NULL")
                AppLog.infoLog("Application Database: AlterMediaInfoModelTable")
            }
        }
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE UserEntity ADD classroomUserId INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}