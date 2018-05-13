package com.raka.runlover

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private var mHistory: List<RunData>, private var mListener: OnItemClickListener): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: RunData)
    }

    class HistoryViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        public var textDate: TextView
        public var textDuration: TextView
        public var textDistance: TextView

        init {
            textDate = view.findViewById(R.id.text_date)
            textDuration = view.findViewById(R.id.text_duration)
            textDistance = view.findViewById(R.id.text_distance)
        }

        public fun setOnClickListener(listener: OnItemClickListener, data: RunData) {
            itemView.setOnClickListener {
                listener.onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = inflater.inflate(R.layout.view_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.textDistance.text = String.format("%.2f m", mHistory[position].getDistance())
        holder.textDuration.text = SimpleDateFormat("mm:ss:SSS").format(Date(mHistory[position].getDurationInMillis()))
        holder.textDate.text = SimpleDateFormat("dd/MM/yyyy").format(Date(mHistory[position].getDateInMillis()))

        holder.setOnClickListener(mListener, mHistory[position])
    }

    override fun getItemCount(): Int {
        return mHistory.count()
    }
}