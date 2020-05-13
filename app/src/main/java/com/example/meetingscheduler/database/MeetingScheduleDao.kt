package com.example.meetingscheduler.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.meetingscheduler.models.MeetingSchedule
import com.example.meetingscheduler.models.MeetingSchedule.Companion.TABLE_NAME

@Dao
interface MeetingScheduleDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAllMeetings() : List<MeetingSchedule>

    @Query("SELECT * FROM $TABLE_NAME WHERE meetingDate = :date ORDER BY startTime asc")
    suspend fun getMeetingsByDate(date : String) : List<MeetingSchedule>

    @Insert
    suspend fun insertMeetings(vararg meetingSchedule: MeetingSchedule)

    @Insert
    suspend fun insertAll(vararg meetingSchedule: MeetingSchedule)

    @Delete
    suspend fun delete(meetingSchedule : MeetingSchedule)
}