package com.example.meetingscheduler.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.example.meetingscheduler.coroutine.BaseActivity
import com.example.meetingscheduler.R
import com.example.meetingscheduler.database.AppDatabase
import com.example.meetingscheduler.database.MeetingScheduleDao
import com.example.meetingscheduler.models.MeetingSchedule
import kotlinx.android.synthetic.main.activity_schedule_meeting.*
import kotlinx.android.synthetic.main.top_bar_schedule_meeting_layout.*
import kotlinx.coroutines.launch

class ScheduleMeetingActivity : BaseActivity() {
    private lateinit var meetingScheduleDao: MeetingScheduleDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)

        meetingScheduleDao = AppDatabase(this).meetingScheduleDao()
        button_back.setOnClickListener {
            finish()
        }
        meeting_date.setOnClickListener {
            showDatePickerDialog(meeting_date)
        }

        meeting_start_time.setOnClickListener {
            showTimePickerDialog(meeting_start_time)
        }

        meeting_end_time.setOnClickListener {
            showTimePickerDialog(meeting_end_time)
        }

        button_submit_meeting.setOnClickListener {
           addMeeting()
        }
    }

    private fun showTimePickerDialog(textView: TextView) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showDatePickerDialog(textView: TextView) {
        val c = Calendar.getInstance()
        val calendarYear = c.get(Calendar.YEAR)
        val calendarMonth = c.get(Calendar.MONTH)
        val calendarDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                textView.text = ("$dayOfMonth-${month+1}-$year".trim())
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.show()
    }

    private fun addMeeting() {
        val meetingDate = meeting_date.text.toString()
        val startTime = meeting_start_time.text.toString()
        val endTime = meeting_end_time.text.toString()
        val description = meeting_description.text.toString()
        val meetingSchedule = MeetingSchedule(startTime, endTime, meetingDate, description)
        launch {
            baseContext?.let{
                AppDatabase(it).meetingScheduleDao().insertMeetings(meetingSchedule)
                Toast.makeText(this@ScheduleMeetingActivity, "Meeting added!", Toast.LENGTH_SHORT).show()
            }
        }
        val intent = Intent(this, MeetingsActivity::class.java)
        startActivity(intent)
        finish()
    }
    companion object{
        const val ADDED_MEETING = "addedMeeting"
        const val ADDED_CODE = 101
    }
}
