package com.raka.runlover

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class HistoryAdapter(private var mHistory: List<RunData>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        public var textDate: TextView
        public var textDuration: TextView
        public var textDistance: TextView

        init {
            textDate = view.findViewById(R.id.text_date)
            textDuration = view.findViewById(R.id.text_duration)
            textDistance = view.findViewById(R.id.text_distance)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = inflater.inflate(R.layout.view_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        //holder.textDuration
    }

    override fun getItemCount(): Int {
        return mHistory.count()
    }
}