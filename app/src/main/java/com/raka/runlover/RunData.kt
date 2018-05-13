package com.raka.runlover

import com.google.android.gms.maps.model.LatLng
import kotlin.collections.HashMap

class RunData(map: Map<String, Any>) {
    companion object {
        const val KEY_DATE = "date"
        const val KEY_START = "start"
        const val KEY_FINISH = "finish"
        const val KEY_DURATION = "duration"
        const val KEY_DISTANCE = "distance"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"

        fun CreateMap(dateinMillis: Long, duration: Long, distance: Float,
                      start: LatLng?, finish: LatLng?): HashMap<String, Any> {

            return hashMapOf(
                    KEY_DATE to dateinMillis,
                    KEY_DURATION to duration,
                    KEY_DISTANCE to distance,
                    KEY_START to hashMapOf(
                            KEY_LATITUDE to start!!.latitude,
                            KEY_LONGITUDE to start!!.longitude
                    ),
                    KEY_FINISH to hashMapOf(
                            KEY_LATITUDE to finish!!.latitude,
                            KEY_LONGITUDE to finish!!.longitude
                    )
            )
        }
    }

    private var mDate: Long = 0
    private var mDuration: Long = 0
    private var mDistance: Float = 0f
    private var mStart: LatLng = LatLng(0.0, 0.0)
    private var mFinish: LatLng = LatLng(0.0, 0.0)

    init {
        var start = map[KEY_START] as Map<*, *>
        var finish = map[KEY_FINISH] as Map<*, *>
        var startLatitude: Double = 0.0
        var startLongitude: Double = 0.0
        var finishLatitude: Double = 0.0
        var finishLongitude: Double = 0.0

        when (map[KEY_DATE]) {
            is Int -> mDate = (map[KEY_DATE] as Int).toLong()
            is Long -> mDate = map[KEY_DATE] as Long
        }

        when (map[KEY_DURATION]) {
            is Int -> mDuration = (map[KEY_DURATION] as Int).toLong()
            is Long -> mDuration = map[KEY_DURATION] as Long
        }

        when (map[KEY_DISTANCE]) {
            is Int -> mDistance = (map[KEY_DISTANCE] as Int).toFloat()
            is Long -> mDistance = (map[KEY_DISTANCE] as Long).toFloat()
            is Double -> mDistance = (map[KEY_DISTANCE] as Double).toFloat()
            is Float -> mDistance = map[KEY_DISTANCE] as Float
        }

        when (start[KEY_LATITUDE]) {
            is Int -> startLatitude = (start[KEY_LATITUDE] as Int).toDouble()
            is Long -> startLatitude = (start[KEY_LATITUDE] as Long).toDouble()
            is Double -> startLatitude = start[KEY_LATITUDE] as Double
        }

        when (start[KEY_LONGITUDE]) {
            is Int -> startLongitude = (start[KEY_LONGITUDE] as Int).toDouble()
            is Long -> startLongitude = (start[KEY_LONGITUDE] as Long).toDouble()
            is Double -> startLongitude = start[KEY_LONGITUDE] as Double
        }

        when (finish[KEY_LATITUDE]) {
            is Int -> finishLatitude = (finish[KEY_LATITUDE] as Int).toDouble()
            is Long -> finishLatitude = (finish[KEY_LATITUDE] as Long).toDouble()
            is Double -> finishLatitude = finish[KEY_LATITUDE] as Double
        }

        when (finish[KEY_LONGITUDE]) {
            is Int -> finishLongitude = (finish[KEY_LONGITUDE] as Int).toDouble()
            is Long -> finishLongitude = (finish[KEY_LONGITUDE] as Long).toDouble()
            is Double -> finishLongitude = finish[KEY_LONGITUDE] as Double
        }

        mStart = LatLng(startLatitude, startLongitude)
        mFinish = LatLng(finishLatitude, finishLongitude)
    }

    public fun getDateInMillis() : Long {
        return mDate
    }

    public fun getDurationInMillis() : Long {
        return mDuration
    }

    public fun getDistance() : Float {
        return mDistance
    }

    public fun getStartCoordinate() : LatLng {
        return mStart
    }

    public fun getFinishCoordinate() : LatLng {
        return mFinish
    }
}