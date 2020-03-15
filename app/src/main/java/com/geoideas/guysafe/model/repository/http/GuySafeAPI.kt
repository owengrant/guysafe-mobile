package com.geoideas.guysafe.model.repository.http

import com.geoideas.guysafe.model.entity.Zone
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET

interface GuySafeAPI {

    @GET("/api/v1/zones")
    fun zones(): Call<JsonArray>
}