package com.example.meetingscheduler.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.meetingscheduler.models.MeetingSchedule.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize
import java.util.*

/*
Model MeetingSchedule class having startTime, endTime, meetingDate, description all as String
variables and giving it annotation of Entity making it a table in our app database having a
autogenerated meetingId working as a Primary Key
 */
@Parcelize
@Entity(tableName = TABLE_NAME)
data class MeetingSchedule(
    val startTime: Date?,
    val endTime: Date?,
    val meetingDate: Date?,
    val description: String,
    @PrimaryKey(autoGenerate = true) val meetingId: Int? = null
) : Parcelable {
    companion object {
        const val TABLE_NAME = "meeting_schedules"
    }
}