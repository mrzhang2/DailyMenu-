package com.dailymenu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    
    object Home : Screen("home")
    object Discover : Screen("discover")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }

    object Work : Screen("work")

    object WorkDetail : Screen("work/{workId}") {
        fun createRoute(workId: Long) = "work/$workId"
    }
    
    object Settings : Screen("settings")
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    companion object {
        val items = listOf(
            BottomNavItem(
                route = Screen.Home.route,
                icon = Icons.Default.Home,
                label = "首页"
            ),
            BottomNavItem(
                route = Screen.Discover.route,
                icon = Icons.Default.Search,
                label = "发现"
            ),
            BottomNavItem(
                route = Screen.Favorites.route,
                icon = Icons.Default.Favorite,
                label = "收藏"
            ),
            BottomNavItem(
                route = Screen.Profile.route,
                icon = Icons.Default.Person,
                label = "我的"
            )
        )
    }
}
