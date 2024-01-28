package com.example.auto_attentatino.api.service

import retrofit2.Call
import retrofit2.http.GET

interface TestApi {
    @GET("test")
    fun testing(): Call<String>
}