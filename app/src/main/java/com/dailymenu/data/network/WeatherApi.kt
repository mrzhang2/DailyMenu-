package com.dailymenu.data.network

import com.dailymenu.data.model.QWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    
    // 和风天气实时天气API
    @GET("v7/weather/now")
    suspend fun getCurrentWeather(
        @Query("location") location: String,  // 经纬度格式：经度,纬度
        @Query("key") apiKey: String
    ): Response<QWeatherResponse>

    companion object {
        const val BASE_URL = "https://devapi.qweather.com/"
        // 使用免费的和风天气开发版API，用户需要自行申请API Key
        const val DEFAULT_API_KEY = "YOUR_QWEATHER_API_KEY"
    }
}