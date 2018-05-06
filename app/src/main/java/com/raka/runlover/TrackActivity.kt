package com.raka.runlover

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class TrackActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mTextTimer: TextView
    private lateinit var mTextDistance: TextView

    private var mHandler: Handler = Handler()
    private var mStart: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        /*mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        mLocationProvider.lastLocation*/

        //Needs: map, timer, start and stop button
        mTextTimer = findViewById(R.id.text_duration)

        mStart = System.currentTimeMillis()
        mHandler.post(object: Runnable {
            override fun run() {
                var duration: Long = System.currentTimeMillis() - mStart
                var formatter: SimpleDateFormat = SimpleDateFormat("mm:ss:SSS")

                mTextTimer.text = formatter.format(Date(duration))

                mHandler.postDelayed(this, 20)
            }
        })

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        var buttonAction: Button = findViewById(R.id.button_track)
        buttonAction.setOnClickListener {
            var detailIntent: Intent = Intent(this, DetailActivity::class.java)
            detailIntent.putExtra(DetailActivity.EXTRA_DISTANCE, 122f)
            detailIntent.putExtra(DetailActivity.EXTRA_DURATION, 13362.toLong())
            detailIntent.putExtra(DetailActivity.EXTRA_START_LAT, -6.157519)
            detailIntent.putExtra(DetailActivity.EXTRA_START_LONG, 106.908021)
            detailIntent.putExtra(DetailActivity.EXTRA_FINISH_LAT, -6.163966)
            detailIntent.putExtra(DetailActivity.EXTRA_FINISH_LONG, 106.903316)
            detailIntent.putExtra(DetailActivity.EXTRA_DATE, Calendar.getInstance().time.time)

            finish()
            startActivity(detailIntent)
        }

        var mapView: MapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            map.setMinZoomPreference(18f)
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false

            //Get location
            var locationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            /*locationProvider.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val coordinate = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18f))
                }
            }*/

            var request: LocationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                fastestInterval = 3000
                interval = 10000
            }

            /*var builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(request)
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())*/

            locationProvider.requestLocationUpdates(request, object: LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    if (result != null) {
                        var coordinate = LatLng(result.lastLocation.latitude, result.lastLocation.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
                    }
                }
            }, Looper.myLooper())
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
