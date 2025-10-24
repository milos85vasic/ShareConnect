package com.shareconnect.plexconnect.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "server_list") {
        composable("server_list") {
            ServerListScreen(navController)
        }
        composable("add_server") {
            AddServerScreen(navController)
        }
        composable("authenticate") {
            AuthenticationScreen(navController)
        }
        composable("libraries/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")?.toLongOrNull()
            if (serverId != null) {
                LibraryListScreen(navController, serverId)
            }
        }
        composable("media/{serverId}/{libraryKey}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")?.toLongOrNull()
            val libraryKey = backStackEntry.arguments?.getString("libraryKey")
            if (serverId != null && libraryKey != null) {
                MediaListScreen(navController, serverId, libraryKey)
            }
        }
        composable("media_detail/{serverId}/{ratingKey}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")?.toLongOrNull()
            val ratingKey = backStackEntry.arguments?.getString("ratingKey")
            if (serverId != null && ratingKey != null) {
                MediaDetailScreen(navController, serverId, ratingKey)
            }
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}