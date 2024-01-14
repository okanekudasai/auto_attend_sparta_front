package com.example.auto_attentatino.api.service

import com.example.auto_attentatino.api.dto.LoginDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("initiating")
    suspend fun logining(@Body logindto: LoginDto): Response<String>
}