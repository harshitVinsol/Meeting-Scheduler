package com.example.meetingscheduler.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.meetingscheduler.models.MeetingSchedule
import com.example.meetingscheduler.models.MeetingSchedule.Companion.TABLE_NAME

/*
Meeting ScheduleDao that acts as a DAO between RoomDatabase and App Database
 */
@Dao
interface MeetingScheduleDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE meetingDate = :date ORDER BY startTime asc")
    suspend fun getMeetingsByDate(date: String): List<MeetingSchedule>

    @Insert
    suspend fun insertMeetings(vararg meetingSchedule: MeetingSchedule)

    @Insert
    suspend fun insertAll(vararg meetingSchedule: MeetingSchedule)

    @Delete
    suspend fun delete(meetingSchedule: MeetingSchedule)

    @Query("SELECT CASE WHEN EXISTS (SELECT * FROM $TABLE_NAME WHERE :targetEndTime <= startTime AND :targetStartTime >= endTime AND meetingDate = :targetDate) THEN 0 ELSE 1 END")
    fun isTimingOverlapping(
        targetDate: String,
        targetStartTime: String,
        targetEndTime: String
    ): Boolean
}