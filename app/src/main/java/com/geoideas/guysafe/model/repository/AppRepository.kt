package com.geoideas.guysafe.model.repository

import android.content.Context
import android.util.Log
import com.geoideas.guysafe.model.AppDatabase
import com.geoideas.guysafe.model.dto.ZoneDto
import com.geoideas.guysafe.model.entity.Zone
import com.geoideas.guysafe.model.repository.http.GuySafeService.api
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class AppRepository(private val ctx: Context) {
    private val db = AppDatabase.getInstance(ctx)
    private val gson = Gson()

    fun readZones() =  gson.fromJson(db.zonesDao().read().zones, JsonArray::class.java)
        .map { gson.fromJson(it, ZoneDto::class.java) }

    fun refreshZones() {
        val response = api.zones().execute()
        if(response.isSuccessful) {
            val geojson = response.body()
            db.zonesDao().deleteAll()
            val zone = Zone(null, geojson.toString())
            db.zonesDao().insert(zone)
        }

    }

}