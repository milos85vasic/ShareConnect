/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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