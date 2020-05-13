package com.example.meetingscheduler.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.meetingscheduler.models.MeetingSchedule.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class MeetingSchedule(
    val startTime: String,
    val endTime: String,
    val meetingDate: String,
    val description: String,
    @PrimaryKey(autoGenerate = true) val meetingId : Int? = null
){
    companion object{
        const val TABLE_NAME = "meeting_schedules"
    }
}