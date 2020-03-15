package com.geoideas.guysafe.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

open class Locator(private val context: Context) {

    private val TAG = "Locator"

    val lP = LocationServices.getFusedLocationProviderClient(context)
    val gF = LocationServices.getGeofencingClient(context)

    fun hasPerms(context: Context) = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    open fun currentLocation(callBack: (loc: Location) -> Unit){
        if(!hasPerms(context)) {
            Log.d(TAG, "missing permission")
            return
        }
        val lcb = defaultLCB(callBack, true)
        Tracker.listen(context, lP, lcb)
    }

    open fun startTracking(callBack: (loc: Location) -> Unit){
        if(!hasPerms(context)) {
            Log.d(TAG, "missing permission")
            return
        }
        val lcb = defaultLCB(callBack, false)
        Tracker.listen(context, lP, lcb)
    }

    open fun defaultLCB(callBack: (loc: Location) -> Unit, oneShot: Boolean): LocationCallback {
        lateinit var lcb: LocationCallback
        lcb = object: LocationCallback(){
            override fun onLocationResult(res: LocationResult?) {
                //do something if location is null
                res ?: return
                callBack(res.lastLocation)
                if(oneShot) lP.removeLocationUpdates(lcb)
            }
        }
        return lcb
    }
}
