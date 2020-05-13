package com.example.meetingscheduler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.meetingscheduler.models.MeetingSchedule

@Database(entities = [(MeetingSchedule::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meetingScheduleDao(): MeetingScheduleDao

    companion object {
        private const val NAME_OF_DATABASE = "meetingscheduledb"

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            NAME_OF_DATABASE
        ).build()

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

