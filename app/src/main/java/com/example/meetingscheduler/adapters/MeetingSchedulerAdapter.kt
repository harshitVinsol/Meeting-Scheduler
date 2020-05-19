package com.example.meetingscheduler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetingscheduler.R
import com.example.meetingscheduler.models.MeetingSchedule

/*
A Meeting Scheduler Adapter for the recycler view that loads the meetings
 */
class MeetingSchedulerAdapter(private var meetingsList: List<MeetingSchedule>) :
    RecyclerView.Adapter<MeetingSchedulerViewHolder>() {
    /*
    A public function that assigns the list in function parameters to the meetingsList of the adapter and call notifyDataSetChanged()
     */
    fun setMeetingScheduleList(list: List<MeetingSchedule>) {
        meetingsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingSchedulerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meeting, parent, false)

        return MeetingSchedulerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return meetingsList.size
    }

    override fun onBindViewHolder(holder: MeetingSchedulerViewHolder, position: Int) {
        val meeting = meetingsList[position]
        holder.startTime.text = "${meeting.startTime?.hours}:${meeting.startTime?.minutes}"
        holder.endTime.text = "${meeting.endTime?.hours}:${meeting.endTime?.minutes}"
        holder.description.text = meeting.description.trim()
    }
}

/*
MeetingSchedulerViewHolder working as a ViewHolder for the MeetingSchedulerAdapter
 */
class MeetingSchedulerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val startTime: TextView = itemView.findViewById(R.id.timing_start)
    val endTime: TextView = itemView.findViewById(R.id.timing_end)
    val description: TextView = itemView.findViewById(R.id.description)
}

