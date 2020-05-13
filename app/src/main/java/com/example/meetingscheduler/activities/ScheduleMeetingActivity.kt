package com.example.meetingscheduler.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.meetingscheduler.coroutine.BaseActivity
import com.example.meetingscheduler.R
import com.example.meetingscheduler.database.AppDatabase
import com.example.meetingscheduler.models.MeetingSchedule
import kotlinx.android.synthetic.main.activity_meetings.*
import kotlinx.android.synthetic.main.activity_schedule_meeting.*
import kotlinx.android.synthetic.main.top_bar_schedule_meeting_layout.*
import kotlinx.coroutines.launch

class ScheduleMeetingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)
        launch {
            baseContext?.let {
                val result = AppDatabase(it).meetingScheduleDao()
                    .isTimingOverlapping(
                        meeting_date.text.toString().trim(),
                        meeting_start_time.text.toString().trim(),
                        meeting_end_time.text.toString().trim()
                    )
                Log.i("123", result)
            }
        }

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
            validateAll()
            addMeeting()
        }
    }

    private fun showTimePickerDialog(textView: TextView) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            textView.text = SimpleDateFormat("HH:mm aa").format(cal.time)
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
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                textView.text = ("$dayOfMonth-${month + 1}-$year".trim())
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun validateInput(): Boolean {
        val x = isSlotAvailable(
            meeting_date.text.toString().trim(),
            meeting_start_time.text.toString().trim(),
            meeting_end_time.text.toString().trim()
        )
        Log.i("1234", "$x")
        return (validateDate() && validateStartTime() && validateEndTime() && validateDescription() && validateTime() && x)
    }


    private fun validateDate(): Boolean {
        return if (meeting_date.text.toString() == resources.getString(R.string.meeting_date_text)) {
            meeting_date.error = "Select a Meeting Date"
            Toast.makeText(this, "Select a Meeting Date", Toast.LENGTH_SHORT).show()
            false
        } else {
            meeting_date.error = null
            true
        }
    }

    private fun validateStartTime(): Boolean {
        return if (meeting_start_time.text.toString() == resources.getString(R.string.meeting_start_time_text)) {
            meeting_start_time.error = "Select a Start time for Meeting"
            false
        } else {
            meeting_start_time.error = null
            true
        }
    }

    private fun validateEndTime(): Boolean {
        return if (meeting_end_time.text.toString() == resources.getString(R.string.meeting_end_time_text)) {
            meeting_end_time.error = "Select a End Time for Meeting"
            false
        } else {
            meeting_end_time.error = null
            true
        }
    }

    private fun validateDescription(): Boolean {
        return if (meeting_description.text.toString().trim().isEmpty()) {
            meeting_description.error = "Enter a proper description"
            meeting_description.requestFocus()
            false
        } else {
            meeting_description.error = null
            true
        }
    }

    private fun validateTime(): Boolean {
        return if (meeting_start_time.text.toString() >= meeting_end_time.text.toString()) {
            Toast.makeText(this, "Meeting should end after it will start!", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }
    }

    private fun validateAll() {
        validateDate()
        validateStartTime()
        validateEndTime()
        validateDescription()
    }

    private fun isSlotAvailable(
        targetDate: String,
        targetStartTime: String,
        targetEndTime: String
    ): Boolean {
        var result = ""
        launch {
            baseContext?.let {
                result = AppDatabase(it).meetingScheduleDao()
                    .isTimingOverlapping(targetDate, targetStartTime, targetEndTime)
            }
        }
        return if (result == "false") {
            Toast.makeText(this, "This is slot is not available for this date", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }

    }

    private fun addMeeting() {
        if (validateInput()) {
            val meetingDate = meeting_date.text.toString().trim()
            val startTime = meeting_start_time.text.toString().trim()
            val endTime = meeting_end_time.text.toString().trim()
            val description = meeting_description.text.toString().trim()
            val meetingSchedule = MeetingSchedule(startTime, endTime, meetingDate, description)
            launch {
                baseContext?.let {
                    AppDatabase(it).meetingScheduleDao().insertMeetings(meetingSchedule)
                    Toast.makeText(
                        this@ScheduleMeetingActivity,
                        "Meeting added!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            val intent = Intent(this, MeetingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
