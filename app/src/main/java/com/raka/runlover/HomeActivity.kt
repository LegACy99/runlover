package com.raka.runlover

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import com.cloudant.sync.event.Subscribe
import com.cloudant.sync.event.notifications.ReplicationCompleted
import com.cloudant.sync.event.notifications.ReplicationErrored
import com.cloudant.sync.replication.Replicator
import com.cloudant.sync.replication.ReplicatorBuilder
import kotlinx.android.synthetic.main.activity_home.*
import java.net.URI

class HomeActivity : AppCompatActivity() {
    private val BACKEND_URL: String = ""
    private val DATABASE_NAME: String = "main_database"

    private var mStore: DocumentStore? = null
    private var mDownloader: Replicator? = null

    private var mHistory: ArrayList<RunData> = ArrayList<RunData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        var recycler: RecyclerView = findViewById(R.id.recycler_history)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler.setHasFixedSize(true)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = HistoryAdapter(mHistory, object : HistoryAdapter.OnItemClickListener{
            override fun onItemClick(data: RunData) {
                var dataMap: HashMap<String, Any> = RunData.CreateMap(data.getDateInMillis(), data.getDurationInMillis(),
                        data.getDistance(), data.getStartCoordinate(), data.getFinishCoordinate())

                var detailIntent: Intent = Intent(applicationContext, DetailActivity::class.java)
                detailIntent.putExtra(DetailActivity.EXTRA_DATA, dataMap)

                startActivity(detailIntent)
            }
        })

        fab.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, TrackActivity::class.java))
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        mStore = DocumentStore.getInstance(getDir("DocumentStore", Context.MODE_PRIVATE))

        var databaseURI: URI = URI("$BACKEND_URL/$DATABASE_NAME")
        mDownloader = ReplicatorBuilder.pull().from(databaseURI).to(mStore).build()
        mDownloader!!.eventBus.register(this)
        mDownloader!!.start()
    }

    override fun onStart() {
        super.onStart()

        readHistoryData()
    }

    @Subscribe
    public fun onComplete(event: ReplicationCompleted) {
        mDownloader!!.eventBus.unregister(this)
        mDownloader = null

        readHistoryData()
    }

    @Subscribe
    public fun onError(event: ReplicationErrored) {
        mDownloader!!.eventBus.unregister(this)
        mDownloader = null
    }

    private fun readHistoryData() {
        if (mStore != null) {
            var database = mStore!!.database();

            if (mHistory.count() != database.documentCount) {
                mHistory.clear()

                var documentList: List<DocumentRevision> = database.read(0, database.documentCount, true)
                for (document in documentList) {
                    mHistory.add(RunData(document.body.asMap()))
                }

                var recycler: RecyclerView = findViewById(R.id.recycler_history)
                recycler.adapter.notifyDataSetChanged()
                recycler.recycledViewPool.clear()
                recycler.invalidate()
            }
        }
    }
}
