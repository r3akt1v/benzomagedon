package com.benzomageddon.data.repository

import com.benzomageddon.data.network.ExchangeRateApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class RatesRepository @Inject constructor(
    private val api: ExchangeRateApi
) {

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    suspend fun refresh() {
        try {
            val response = api.getRates()
            if (response.result == "success") {
                _rates.value = response.rates
            }
        } catch (_: Exception) {
            // offline — keep previous rates
        }
    }
}
