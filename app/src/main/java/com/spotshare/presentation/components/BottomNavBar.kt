package com.spotshare.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spotshare.presentation.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavigationItem("Feed", Screen.Feed.route, Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("Explore", Screen.Explore.route, Icons.Filled.Search, Icons.Outlined.Search),
        NavigationItem("Add", Screen.CreatePost.route, Icons.Filled.AddBox, Icons.Outlined.AddBox),
        NavigationItem("Reels", Screen.Reels.route, Icons.Filled.Movie, Icons.Outlined.Movie),
        NavigationItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon, 
                        contentDescription = item.title 
                    ) 
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
