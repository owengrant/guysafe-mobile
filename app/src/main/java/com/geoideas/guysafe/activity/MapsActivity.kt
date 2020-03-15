package com.geoideas.guysafe.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.geoideas.guysafe.R
import com.geoideas.guysafe.model.repository.AppRepository
import com.geoideas.guysafe.service.LocationService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = MapsActivity::class.java.simpleName

    val LOCATION_PERMISSIONS = 1
    val SMS_SEND_PERMISSIONS = 2
    val FILE_PERMISSIONS = 3
    var hasLocation = false
    private lateinit var mMap: GoogleMap
    private lateinit var repo: AppRepository
    private val executors = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        repo = AppRepository(this)
        resolvePermissions()
        startService()
        executors.scheduleAtFixedRate(::loadData, 5, 30, TimeUnit.SECONDS)
    }

    override fun onResume() {
        super.onResume()
        executors.scheduleAtFixedRate(::loadData, 5, 30, TimeUnit.SECONDS)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasLocation = true
            startLocationService()
        }
    }

    private fun loadData() {
        val zones = repo.readZones()
        if(zones.isNotEmpty()) {
            val circleOptions = zones.map {
                CircleOptions()
                    .center(LatLng(it.latitude, it.longitude))
                    .radius(it.radius.toDouble())
                    .fillColor(Color.argb(75,250,0,0))
                    .strokeColor(Color.argb(75,250,0,0))
                    .clickable(true)

            }
            this.runOnUiThread {
                mMap.clear()
                circleOptions.forEach { mMap.addCircle(it) }
            }
        }
    }

    private fun resolvePermissions() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        hasLocation = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocation) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(this, "Permmisions needed for location service", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(
                this,
                perms,
                LOCATION_PERMISSIONS
            )
        } else startLocationService()
    }

    private fun startService() {
        if (hasLocation) startLocationService()
    }

    private fun startLocationService() {
        Log.d("Maps", "sta")
        Intent(this, LocationService::class.java).also {
            ContextCompat.startForegroundService(this, it)
        }
    }


}
