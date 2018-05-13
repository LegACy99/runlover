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
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val EXTRA_DATA = "data"
    }

    private lateinit var mData: RunData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        var extra = intent.getSerializableExtra(EXTRA_DATA) as Map<String, Any>
        mData = RunData(extra)

        var durationString = SimpleDateFormat("mm:ss:SSS").format(Date(mData.getDurationInMillis()))

        var mapView: MapView = findViewById(R.id.map)
        var textDate: TextView = findViewById(R.id.text_date)
        var textDuration: TextView = findViewById(R.id.text_duration)
        var textDistance: TextView = findViewById(R.id.text_distance)

        textDistance.text = String.format("Distance: %.2f m", mData.getDistance())
        textDuration.text = "Duration: $durationString"
        textDate.text = SimpleDateFormat("dd/MM/yyyy").format(Date(mData.getDateInMillis()))

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            map.setMinZoomPreference(16f)
            map.uiSettings.isZoomControlsEnabled = false
            map.uiSettings.isScrollGesturesEnabled = false

            map.addMarker(MarkerOptions().position(mData.getStartCoordinate()))
            map.addMarker(MarkerOptions().position(mData.getFinishCoordinate()))

            var center: LatLng = LatLng((mData.getStartCoordinate().latitude + mData.getFinishCoordinate().latitude) / 2.0,
                                        (mData.getStartCoordinate().longitude + mData.getFinishCoordinate().longitude) / 2.0)
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
