package com.example.meetingscheduler.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.meetingscheduler.coroutine.BaseActivity
import com.example.meetingscheduler.R
import com.example.meetingscheduler.activities.MeetingsActivity.Companion.FORMATTED_TOP_BAR_DATE
import com.example.meetingscheduler.activities.MeetingsActivity.Companion.TOP_BAR_DATE
import com.example.meetingscheduler.database.AppDatabase
import com.example.meetingscheduler.models.MeetingSchedule
import kotlinx.android.synthetic.main.activity_schedule_meeting.*
import kotlinx.android.synthetic.main.top_bar_schedule_meeting_layout.*
import kotlinx.coroutines.*
import java.util.*

/*
An activity to Schedule a meeting for a date with start time, end time and a description
*/
class ScheduleMeetingActivity : BaseActivity() {
    private var formattedDate = Date(Calendar.getInstance().timeInMillis)
    private var formattedStartTime = Date(Calendar.getInstance().timeInMillis)
    private var formattedEndTime = Date(Calendar.getInstance().timeInMillis)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)

        formattedDate = intent.getSerializableExtra(FORMATTED_TOP_BAR_DATE) as Date
        meeting_date.text = "${formattedDate.date}-${formattedDate.month+1}-${formattedDate.year+1900}"

        button_back.setOnClickListener {
            finish()
        }

        meeting_date.setOnClickListener {
            showDatePickerDialog(meeting_date)
        }

        meeting_start_time.setOnClickListener {
            formattedStartTime = showTimePickerDialog(meeting_start_time)
        }

        meeting_end_time.setOnClickListener {
            formattedEndTime = showTimePickerDialog(meeting_end_time)
        }

        button_submit_meeting.setOnClickListener {
            validateAll()
            addMeeting()
        }
    }

    /*
    A function to build and show a time picker dialog
     */
    private fun showTimePickerDialog(textView: TextView): Date {
        val cal = Calendar.getInstance()
        var formattedTime = Date(cal.timeInMillis)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            formattedTime = Date(cal.timeInMillis)
            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
        return formattedTime
    }

    /*
    A function to build and show a Date picker dialog
     */
    private fun showDatePickerDialog(textView: TextView) {
        val c = Calendar.getInstance()
        val calendarYear = c.get(Calendar.YEAR)
        val calendarMonth = c.get(Calendar.MONTH)
        val calendarDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                c[Calendar.YEAR] = year
                c[Calendar.MONTH] = month
                c[Calendar.DATE] = dayOfMonth
                formattedDate = Date(c.timeInMillis)
                textView.text = "$dayOfMonth-${month+1}-$year"
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    /*
    A Boolean function to validate all the fields as well as to check if the meeting slot is available
     */
    private fun validateInput(): Boolean {
        return (validateDate() && validateStartTime() && validateEndTime() && validateDescription() && validateTime() && checkSlotAvailable())
    }

    /*
    A boolean validation function to check if the timing entered in the fields are available or not
     */
    private fun checkSlotAvailable(): Boolean {
        val result = isSlotAvailable(
            formattedDate,
            formattedStartTime,
            formattedEndTime
        )
        if (!result) {
            Toast.makeText(this, R.string.text_timings_overlap, Toast.LENGTH_SHORT)
                .show()
        }
        return result
    }

    /*
    A boolean function to call isTimingOverlapping query from the database
     */
    private fun isSlotAvailable(
        targetDate: Date,
        targetStartTime: Date,
        targetEndTime: Date
    ): Boolean {
        return runBlocking {
            AppDatabase(baseContext).meetingScheduleDao()
                .isTimingOverlapping(targetDate, targetStartTime, targetEndTime)
        }
    }

    /*
    A Boolean function to validate if a proper date has been picked or not
     */
    private fun validateDate(): Boolean {
        return if (meeting_date.text.toString() == resources.getString(R.string.meeting_date_text)) {
            meeting_date.error = resources.getString(R.string.meeting_date_error)
            Toast.makeText(this, R.string.meeting_date_error, Toast.LENGTH_SHORT).show()
            false
        } else {
            meeting_date.error = null
            true
        }
    }

    /*
    A Boolean function to validate if a proper start time has been picked or not
     */
    private fun validateStartTime(): Boolean {
        return if (meeting_start_time.text.toString() == resources.getString(R.string.meeting_start_time_text)) {
            meeting_start_time.error = resources.getString(R.string.start_time_error)
            false
        } else {
            meeting_start_time.error = null
            true
        }
    }

    /*
    A Boolean function to validate if a proper end time has been picked or not
     */
    private fun validateEndTime(): Boolean {
        return if (meeting_end_time.text.toString() == resources.getString(R.string.meeting_end_time_text)) {
            meeting_end_time.error = resources.getString(R.string.end_time_error)
            false
        } else {
            meeting_end_time.error = null
            true
        }
    }

    /*
    A Boolean function to validate if a proper description has been picked or not
     */
    private fun validateDescription(): Boolean {
        return if (meeting_description.text.toString().trim().isEmpty()) {
            meeting_description.error = resources.getString(R.string.meeting_description_error)
            meeting_description.requestFocus()
            false
        } else {
            meeting_description.error = null
            true
        }
    }

    /*
    A Boolean function that validates that end time of the meeting is after the start time of the meeting
     */
    private fun validateTime(): Boolean {
        return if (meeting_start_time.text.toString() >= meeting_end_time.text.toString()) {
            Toast.makeText(this, R.string.inappropriate_meeting_timings, Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }
    }

    /*
    A function to validate all the field validation functions of the form all at once
     */
    private fun validateAll() {
        validateDate()
        validateStartTime()
        validateEndTime()
        validateDescription()
    }

    /*
    A function to add the meeting if all the validations are true
     */
    private fun addMeeting() {
        if (validateInput()) {
            val description = meeting_description.text.toString().trim()
            val meetingSchedule = MeetingSchedule(formattedStartTime, formattedEndTime, formattedDate, description)
            launch {
                baseContext?.let {
                    AppDatabase(it).meetingScheduleDao().insertMeetings(meetingSchedule)
                    Toast.makeText(
                        this@ScheduleMeetingActivity,
                        R.string.meeting_added,
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