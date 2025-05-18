package com.example.interviewapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewapp.R
import com.example.interviewapp.entity.RecordSource
import com.example.interviewapp.entity.SportRecord

class SportRecordAdapter(private val records: List<SportRecord>) :
    RecyclerView.Adapter<SportRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textTitle)
        val location = itemView.findViewById<TextView>(R.id.textLocation)
        val duration = itemView.findViewById<TextView>(R.id.textDuration)
        val source = itemView.findViewById<TextView>(R.id.textSource)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sport_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.title.text = record.title
        holder.location.text = record.location
        holder.duration.text = "${record.durationMinutes} min"
        holder.source.text = when (record.source) {
            RecordSource.LOCAL -> "Lokální DB"
            RecordSource.REMOTE -> "Backend"
        }
    }

    override fun getItemCount(): Int = records.size
}
