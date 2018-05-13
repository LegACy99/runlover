package com.raka.runlover

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentBody
import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import com.cloudant.sync.event.Subscribe
import com.cloudant.sync.event.notifications.ReplicationCompleted
import com.cloudant.sync.event.notifications.ReplicationErrored
import com.cloudant.sync.replication.Replicator
import com.cloudant.sync.replication.ReplicatorBuilder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_home.*
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TrackActivity : AppCompatActivity() {
    private val BACKEND_URL: String = ""
    private val DATABASE_NAME: String = "main_database"

    private var mTracking: Boolean = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)
        setSupportActionBar(toolbar)

        var mapView: MapView = findViewById(R.id.map)
        var buttonSave: Button = findViewById(R.id.button_save)
        var buttonReset: Button = findViewById(R.id.button_reset)
        var buttonTrack: Button = findViewById(R.id.button_track)
        var textDuration: TextView = findViewById(R.id.text_duration)
        var textDistance: TextView = findViewById(R.id.text_distance)

        var duration: Long = 0
        var distance: Float = 0f;
        var startTime: Long = 0
        var startCoordinate: LatLng? = null
        var finishCoordinate: LatLng? = null

        var map: GoogleMap? = null
        var handler: Handler = Handler()

        var locationListener: LocationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult?) {
                if (result != null) {
                    var previousCoordinate: LatLng? = finishCoordinate
                    finishCoordinate = LatLng(result.lastLocation.latitude, result.lastLocation.longitude)

                    if (startCoordinate == null) {
                        startCoordinate = finishCoordinate;
                    }

                    if (map != null) {
                        map!!.moveCamera(CameraUpdateFactory.newLatLng(finishCoordinate))
                    }

                    if (previousCoordinate != null) {
                        var results: FloatArray = floatArrayOf(0.0f)
                        Location.distanceBetween(previousCoordinate.latitude, previousCoordinate.longitude, finishCoordinate!!.latitude, finishCoordinate!!.longitude, results)

                        distance += results[0]
                        textDistance.text = String.format("%.2f m", distance)
                    }
                }
            }
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap?) {
                map = googleMap;

                if (map != null) {
                    map!!.setMinZoomPreference(16f)
                    map!!.isMyLocationEnabled = true
                    map!!.uiSettings.isZoomControlsEnabled = false
                    map!!.uiSettings.isScrollGesturesEnabled = false
                    map!!.uiSettings.isMyLocationButtonEnabled = false

                    LocationServices.getFusedLocationProviderClient(applicationContext).lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            var coordinate: LatLng = LatLng(location.latitude, location.longitude)
                            map!!.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
                        }
                    }

                    buttonTrack.isEnabled = true
                }
            }
        })

        buttonTrack.setOnClickListener {
            if (mTracking) {
                buttonTrack.visibility = View.INVISIBLE
                buttonReset.visibility = View.VISIBLE
                buttonSave.visibility = View.VISIBLE

                handler.removeCallbacksAndMessages(null)
                LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationListener)

            } else {
                buttonTrack.text = "STOP"

                distance = 0f
                startCoordinate = null
                finishCoordinate = null
                startTime = System.currentTimeMillis()

                handler.post(object: Runnable {
                    override fun run() {
                        duration = System.currentTimeMillis() - startTime
                        textDuration.text = SimpleDateFormat("mm:ss:SSS").format(Date(duration))

                        handler.postDelayed(this, 20)
                    }
                })

                var request: LocationRequest = LocationRequest().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    fastestInterval = 1000
                    interval = 10000
                }

                var builder = LocationSettingsRequest.Builder()
                builder.addLocationRequest(request)
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(request, locationListener, Looper.myLooper())
            }

            mTracking = !mTracking
        }

        buttonReset.setOnClickListener {
            buttonTrack.visibility = View.VISIBLE
            buttonReset.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE

            buttonTrack.text = "START"
            textDuration.text = SimpleDateFormat("mm:ss:SSS").format(Date(0))
            textDistance.text = "0.00 m"
        }

        buttonSave.setOnClickListener {
            var data: HashMap<String, Any> = RunData.CreateMap(startTime, duration, distance, startCoordinate, finishCoordinate)

            var document: DocumentRevision = DocumentRevision()
            document.body = DocumentBodyFactory.create(data)

            var store: DocumentStore = DocumentStore.getInstance(getDir("DocumentStore", Context.MODE_PRIVATE))
            store.database().create(document)

            var databaseURI: URI = URI("$BACKEND_URL/$DATABASE_NAME")
            var uploader: Replicator = ReplicatorBuilder.push().from(store).to(databaseURI).build()
            uploader.start()

            var detailIntent: Intent = Intent(this, DetailActivity::class.java)
            detailIntent.putExtra(DetailActivity.EXTRA_DATA, data)

            finish()
            startActivity(detailIntent)
        }
    }

    override fun onBackPressed() {
        if (!mTracking) {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        findViewById<MapView>(R.id.map).onStart()
    }

    override fun onResume() {
        super.onResume()
        findViewById<MapView>(R.id.map).onResume()
    }

    override fun onPause() {
        findViewById<MapView>(R.id.map).onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        findViewById<MapView>(R.id.map).onStop()
    }

    override fun onDestroy() {
        findViewById<MapView>(R.id.map).onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        findViewById<MapView>(R.id.map).onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        findViewById<MapView>(R.id.map).onLowMemory()
    }
}
