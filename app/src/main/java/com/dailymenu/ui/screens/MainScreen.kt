package com.dailymenu.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dailymenu.ui.navigation.BottomNavItem
import com.dailymenu.ui.navigation.Screen
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.TextSecondary

@Composable
fun MainScreen(
    onNavigateToRecipe: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToWork: () -> Unit
) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onRecipeClick = { mealType, recipeId ->
                        onNavigateToRecipe(recipeId)
                    },
                    onFavoritesClick = {
                        navController.navigate(Screen.Favorites.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onSettingsClick = onNavigateToSettings
                )
            }
            
            composable(Screen.Discover.route) {
                DiscoverScreen(
                    onRecipeClick = onNavigateToRecipe
                )
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    },
                    onRecipeClick = onNavigateToRecipe,
                    onBrowseClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route) {
                            popUpTo(Screen.Profile.route) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToLogin = {
                        // Profile screen handles its own login state
                        // This callback is for when user clicks login from guest mode
                    },
                    onNavigateToWork = {
                        navController.navigate(Screen.Work.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        BottomNavItem.items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryOrange,
                    selectedTextColor = PrimaryOrange,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
    }
}
