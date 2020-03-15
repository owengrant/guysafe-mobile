package com.geoideas.guysafe.model.repository.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GuySafeService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.11:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create<GuySafeAPI>(
        GuySafeAPI::class.java)
}