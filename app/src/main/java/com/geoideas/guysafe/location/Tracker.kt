package com.geoideas.guysafe.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.*

object Tracker {

    private final val TAG = "Tracker"

    @SuppressLint("MissingPermission")
    fun listen(context: Context, lP: FusedLocationProviderClient, lcb: LocationCallback){
        val request = LocationRequest.create().apply {
            interval = 20000
            fastestInterval = 20000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val settings = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .build()
        val settingsCheck = LocationServices.getSettingsClient(context)
            .checkLocationSettings(settings)
        settingsCheck.addOnSuccessListener { lP.requestLocationUpdates(request, lcb, null) }
        settingsCheck.addOnFailureListener { Log.d(TAG, it.localizedMessage) }
    }

}