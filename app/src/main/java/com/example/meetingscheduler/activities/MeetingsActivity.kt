package com.example.meetingscheduler.activities

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetingscheduler.coroutine.BaseActivity
import com.example.meetingscheduler.R
import com.example.meetingscheduler.adapters.MeetingSchedulerAdapter
import com.example.meetingscheduler.database.AppDatabase
import com.example.meetingscheduler.database.MeetingScheduleDao
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
    private lateinit var meetingScheduleDao: MeetingScheduleDao
    private var meetingList : List<MeetingSchedule> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings)

        //meetingScheduleDao = AppDatabase(this).meetingScheduleDao()
        todayDate()
        viewManager = LinearLayoutManager(this)
        meeting_recycler.layoutManager = viewManager

        meetingsAdapter = MeetingSchedulerAdapter(meetingList)
        meeting_recycler.adapter = meetingsAdapter
        //meeting_recycler.hasFixedSize()

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
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_view_divider))
        meeting_recycler.addItemDecoration(dividerItemDecoration)

    }

    private fun todayDate() {
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        currentDate = topBarDate
        val x = currentDate == topBarDate
        Toast.makeText(
            this,
            "Current : $currentDate\nTop Bar : $topBarDate \n$x",
            Toast.LENGTH_SHORT
        ).show()
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.toString().trim())
        enableButtonPrevious()
    }

    private fun nextDate() {
        calendar.add(Calendar.DATE, 1)
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        val x = currentDate == topBarDate
        Toast.makeText(
            this,
            "Current : $currentDate\nTop Bar : $topBarDate \n$x",
            Toast.LENGTH_SHORT
        ).show()
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.toString().trim())
        enableButtonPrevious()
    }

    private fun previousDate() {
        calendar.add(Calendar.DATE, -1)
        topBarDate = simpleDateFormat.format(calendar.time).toString()
        val x = currentDate == topBarDate
        Toast.makeText(
            this,
            "Current : $currentDate\nTop Bar : $topBarDate \n$x",
            Toast.LENGTH_SHORT
        ).show()
        top_date.text = topBarDate
        getMeetingsForDate(topBarDate.toString().trim())
        enableButtonPrevious()
    }

    private fun enableButtonPrevious() {
        button_previous.isEnabled = topBarDate != currentDate
    }

    private fun getMeetingsForDate(date : String){
        launch {
            baseContext?.let { letIt ->
                meetingList = AppDatabase(letIt).meetingScheduleDao().getMeetingsByDate(date)
                meetingList.forEach {
                    Log.i("@harsh", "${it.meetingDate} ${it.startTime} ${it.endTime} ${it.description}")
                }
                meetingsAdapter.setMeetingScheduleList(meetingList)
                meeting_recycler.adapter = meetingsAdapter
                checkIfListEmpty()
            }
        }
    }

    private fun checkIfListEmpty(){
        text_no_meetings.isVisible = meetingList.isEmpty()
    }
    companion object{
        const val REQUEST_CODE = 100
        internal lateinit var currentDate: String
        internal lateinit var topBarDate: String
    }
}
