package com.example.meetingscheduler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.meetingscheduler.models.MeetingSchedule
import com.example.meetingscheduler.models.MeetingSchedule.Companion.TABLE_NAME


/*
This abstract class working as the App Database for the App with @Database annotation having MeetingSchedule as an entity
 */
@Database(entities = [(MeetingSchedule::class)], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meetingScheduleDao(): MeetingScheduleDao

    companion object {
        private const val NAME_OF_DATABASE = "meetingscheduledb"

        /*
        Function that builds and return an instance of Room database
         */
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            NAME_OF_DATABASE
        ).fallbackToDestructiveMigration().build()

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
    }
}

