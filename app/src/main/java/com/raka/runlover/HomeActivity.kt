package com.raka.runlover

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private var mHistory: ArrayList<RunData> = ArrayList<RunData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        //Initially has set support action bar?

        mHistory.add(RunData())
        mHistory.add(RunData())
        mHistory.add(RunData())

        fab.setOnClickListener {
            startActivity(Intent(this, TrackActivity::class.java))
        }

        var recycler: RecyclerView = findViewById(R.id.recycler_history)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = HistoryAdapter(mHistory)
        recycler.setHasFixedSize(true)
    }
}
