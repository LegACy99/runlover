package com.raka.runlover

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val EXTRA_DATE = "date"
        const val EXTRA_DISTANCE = "distance"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_START_LAT = "start_latitude"
        const val EXTRA_START_LONG = "start_longitude"
        const val EXTRA_FINISH_LAT = "finish_latitude"
        const val EXTRA_FINISH_LONG = "finish_longitude"
    }

    private lateinit var mStart: LatLng
    private lateinit var mFinish: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var date: Long = intent.getLongExtra(EXTRA_DATE, 0)
        var duration: Long = intent.getLongExtra(EXTRA_DURATION, 0)
        var distance: Float = intent.getFloatExtra(EXTRA_DISTANCE, 0f)
        mStart = LatLng(intent.getDoubleExtra(EXTRA_START_LAT, 0.0),
                        intent.getDoubleExtra(EXTRA_START_LONG, 0.0))
        mFinish = LatLng(intent.getDoubleExtra(EXTRA_FINISH_LAT, 0.0),
                         intent.getDoubleExtra(EXTRA_FINISH_LONG, 0.0))

        var durationString = SimpleDateFormat("mm:ss:SSS").format(Date(duration))

        var mapView: MapView = findViewById(R.id.map)
        var textDate: TextView = findViewById(R.id.text_date)
        var textDuration: TextView = findViewById(R.id.text_duration)
        var textDistance: TextView = findViewById(R.id.text_distance)

        textDistance.text = "Distance: $distance m"
        textDuration.text = "Duration: $durationString"
        textDate.text = SimpleDateFormat("dd/MM/yyyy").format(Date(date))

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            map.setMinZoomPreference(16f)
            map.uiSettings.isZoomControlsEnabled = false
            map.uiSettings.isScrollGesturesEnabled = false

            map.addMarker(MarkerOptions().position(mStart))
            map.addMarker(MarkerOptions().position(mFinish))

            var center: LatLng = LatLng((mStart.latitude + mFinish.latitude) / 2.0,
                                        (mStart.longitude + mFinish.longitude) / 2.0)
            map.moveCamera(CameraUpdateFactory.newLatLng(center))
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
