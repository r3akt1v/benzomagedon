package com.benzomageddon.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface ExchangeRateApi {

    @GET("v6/latest/RUB")
    suspend fun getRates(): ExchangeRatesResponse
}

data class ExchangeRatesResponse(
    @SerializedName("result") val result: String,
    @SerializedName("rates") val rates: Map<String, Double>
)
