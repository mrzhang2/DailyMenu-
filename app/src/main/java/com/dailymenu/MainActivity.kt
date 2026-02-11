package com.dailymenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dailymenu.data.model.MealType
import com.dailymenu.ui.screens.FavoritesScreen
import com.dailymenu.ui.screens.HomeScreen
import com.dailymenu.ui.screens.RecipeDetailScreen
import com.dailymenu.ui.screens.SettingsScreen
import com.dailymenu.ui.theme.DailyMenuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyMenuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DailyMenuApp()
                }
            }
        }
    }
}

@Composable
fun DailyMenuApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onRecipeClick = { mealType, recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.RecipeDetail.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull() ?: 0L
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onManualWeatherClick = { 
                    // 返回首页并打开手动天气输入
                    navController.popBackStack()
                }
            )
        }
    }
}

// 导航路由定义
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}