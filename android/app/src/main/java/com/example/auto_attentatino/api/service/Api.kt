package com.example.auto_attentatino.api.service

import com.example.auto_attentatino.api.dto.LoginDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("isChromeOpen")
    suspend fun isChromeOpen(): Response<String>

    @GET("toggleChrome")
    suspend fun toggleChrome()

    @POST("initiating")
    suspend fun logining(@Body logindto: LoginDto): Response<String>

    @GET("makeSchedule")
    fun makeSchedule(): Call<String>

    @GET("logout")
    suspend fun logouting()

    @GET("sendToken/{token}")
    fun sendToken(@Path("token") token: String): Call<String>

    @GET("refreshChrome")
    suspend fun refreshChrome(): Response<String>
}