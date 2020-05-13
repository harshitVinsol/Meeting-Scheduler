package com.example.meetingscheduler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetingscheduler.R
import com.example.meetingscheduler.models.MeetingSchedule

class MeetingSchedulerAdapter( private var meetingsList : List<MeetingSchedule>) : RecyclerView.Adapter<MeetingSchedulerAdapter.MeetingSchedulerViewHolder>(){

    fun setMeetingScheduleList( list : List<MeetingSchedule>){
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
        holder.startTime.text = meeting.startTime.toString()
        holder.endTime.text = meeting.endTime.toString()
        holder.description.text = meeting.description.toString()
    }

    class MeetingSchedulerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val startTime : TextView = itemView.findViewById(R.id.timing_start)
        val endTime : TextView  = itemView.findViewById(R.id.timing_end)
        val description :TextView = itemView.findViewById(R.id.description)
    }
}

