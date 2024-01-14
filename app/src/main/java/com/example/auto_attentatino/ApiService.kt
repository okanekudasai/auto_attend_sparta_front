package com.example.auto_attentatino

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("public/ticker/{order_currency}_{payment_currency}")
    fun getCoinTicker(
        @Path("order_currency") orderCurrency: String,
        @Path("payment_currency") payment_currency: String
    ): Call<Ticker>

}