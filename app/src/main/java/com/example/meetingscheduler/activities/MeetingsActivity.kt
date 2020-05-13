package com.example.meetingscheduler.activities

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetingscheduler.coroutine.BaseActivity
import com.example.meetingscheduler.R
import com.example.meetingscheduler.adapters.MeetingSchedulerAdapter
import com.example.meetingscheduler.database.AppDatabase
import com.example.meetingscheduler.models.MeetingSchedule
import kotlinx.android.synthetic.main.activity_meetings.*
import kotlinx.android.synthetic.main.top_bar_meeting_layout.*
import kotlinx.coroutines.launch
import java.util.*

class MeetingsActivity : BaseActivity() {
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var meetingsAdapter: MeetingSchedulerAdapter
    private lateinit var calendar: Calendar
    private lateinit var simpleDateFormat: SimpleDateFormat
    private lateinit var currentDate: String
    private lateinit var topBarDate: String
    private var meetingList: List<MeetingSchedule> = listOf()

    /*
    This is Meeting Activity that opens up with current date at the top which can be changed to next or previous date on which list of meetings assigned to that day are loaded
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings)

        viewManager = LinearLayoutManager(this)
        meeting_recycler.layoutManager = viewManager
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_view_divider))
        meeting_recycler.addItemDecoration(dividerItemDecoration)

        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd-M-yyyy")

        if (savedInstanceState != null) {
            topBarDate = savedInstanceState.getString(TOP_BAR_DATE)!!
            currentDate = savedInstanceState.getString(CURRENT_DATE)!!
            top_date.text = topBarDate
            getMeetingsForDate(topBarDate.trim())
            enableButtonPrevious()
        } else {
            todayDate()
        }
        meetingsAdapter = MeetingSchedulerAdapter(meetingList)
        meeting_recycler.adapter = meetingsAdapter

        button_schedule_meeting.setOnClickListener {
            val intent = Intent(this, ScheduleMeetingActivity::class.java)
            startActivity(intent)
        }

        button_next.setOnClickListener {
            nextDate()
        }

        button_previous.setOnClickListener {
            previousDate()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOP_BAR_DATE, topBarDate)
        outState.putString(CURRENT_DATE, currentDate)
        // outState.putParcelable(CALENDAR_REF, calendar)
    }

    /*
    A function to update current date and assign it to the topBarDate
     */
    private fun todayDate() {
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        currentDate = topBarDate
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.trim())
        enableButtonPrevious()
    }

    /*
    A function to assign a next date to topBarDate
     */
    private fun nextDate() {
        calendar.add(Calendar.DATE, 1)
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.toString().trim())
        enableButtonPrevious()
    }

    /*
    A function to assign a previous date to topBarDate
     */
    private fun previousDate() {
        calendar.add(Calendar.DATE, -1)
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.toString().trim())
        enableButtonPrevious()
    }

    /*
    A function to disable the previous button if the topBarDate is currentDate
     */
    private fun enableButtonPrevious() {
        button_previous.isEnabled = topBarDate != currentDate
    }

    /*
    A function to load meetings for the date in function parameter and assign the adapter to the recycler view
     */
    private fun getMeetingsForDate(date: String) {
        launch {
            baseContext?.let { letIt ->
                meetingList = AppDatabase(letIt).meetingScheduleDao().getMeetingsByDate(date)
                meetingsAdapter.setMeetingScheduleList(meetingList)
                meeting_recycler.adapter = meetingsAdapter
                checkIfListEmpty()
            }
        }
    }

    /*
    A function to check if the list is empty and show text_no_meetings if true
     */
    private fun checkIfListEmpty() {
        text_no_meetings.isVisible = meetingList.isEmpty()
    }

    companion object {
        private const val TOP_BAR_DATE = "top_bar_date"
        private const val CURRENT_DATE = "current_date"
        private const val CALENDAR_REF = "calendar"
    }
}