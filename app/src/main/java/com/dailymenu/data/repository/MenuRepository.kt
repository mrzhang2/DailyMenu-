package com.dailymenu.data.repository

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.dailymenu.data.database.RecipeDao
import com.dailymenu.data.model.*
import com.dailymenu.data.network.NetworkModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class MenuRepository(
    private val context: Context,
    private val recipeDao: RecipeDao
) {
    private val weatherApi = NetworkModule.weatherApi
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    // 获取当前位置天气（自动）
    suspend fun getCurrentLocationWeather(apiKey: String = "YOUR_QWEATHER_API_KEY"): Result<WeatherInfo> {
        return try {
            val location = getCurrentLocation()
            if (location != null) {
                val locationStr = "${location.longitude},${location.latitude}"
                getWeatherByLocation(locationStr, apiKey)
            } else {
                Result.failure(Exception("无法获取位置信息"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 根据城市名获取天气
    suspend fun getWeatherByCity(cityName: String, apiKey: String = "YOUR_QWEATHER_API_KEY"): Result<WeatherInfo> {
        return try {
            // 将城市名转换为经纬度
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocationName(cityName, 1)
            }
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locationStr = "${address.longitude},${address.latitude}"
                getWeatherByLocation(locationStr, apiKey)
            } else {
                Result.failure(Exception("无法找到该城市"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 根据经纬度获取天气
    private suspend fun getWeatherByLocation(location: String, apiKey: String): Result<WeatherInfo> {
        return try {
            val response = weatherApi.getCurrentWeather(location, apiKey)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == "200" && body.now != null) {
                    val weatherInfo = parseWeatherResponse(body)
                    Result.success(weatherInfo)
                } else {
                    Result.failure(Exception("天气API返回错误: ${body?.code}"))
                }
            } else {
                Result.failure(Exception("天气请求失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 获取当前位置
    @Suppress("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    // 解析天气响应
    private fun parseWeatherResponse(response: QWeatherResponse): WeatherInfo {
        val now = response.now!!
        return WeatherInfo(
            temperature = now.temp.toIntOrNull() ?: 20,
            condition = parseWeatherCondition(now.text),
            location = response.fxLink ?: "当前位置",
            humidity = now.humidity.toIntOrNull() ?: 50
        )
    }

    // 解析天气状况
    private fun parseWeatherCondition(text: String): WeatherCondition {
        return when {
            text.contains("晴") -> WeatherCondition.SUNNY
            text.contains("多云") -> WeatherCondition.CLOUDY
            text.contains("阴") -> WeatherCondition.OVERCAST
            text.contains("雨") -> WeatherCondition.RAINY
            text.contains("雪") -> WeatherCondition.SNOWY
            text.contains("雾") || text.contains("霾") -> WeatherCondition.FOGGY
            else -> WeatherCondition.SUNNY
        }
    }

    // 手动创建天气信息
    fun createManualWeather(temperature: Int, condition: WeatherCondition, location: String = "手动设置"): WeatherInfo {
        return WeatherInfo(
            temperature = temperature,
            condition = condition,
            location = location
        )
    }

    // Recipe 相关操作
    fun getRecipesByMealType(mealType: MealType): Flow<List<Recipe>> {
        return recipeDao.getRecipesByMealType(mealType)
    }

    suspend fun getRecipesByMealTypeAndSeason(mealType: MealType, seasons: List<Season>): List<Recipe> {
        return recipeDao.getRecipesByMealTypeAndSeason(mealType, seasons)
    }

    suspend fun getRecipeById(id: Long): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDao.insertRecipe(recipe)
    }

    suspend fun insertRecipes(recipes: List<Recipe>) {
        recipeDao.insertRecipes(recipes)
    }

    fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    suspend fun updateFavoriteStatus(recipeId: Long, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    suspend fun getRecipeCount(): Int {
        return recipeDao.getRecipeCount()
    }
}