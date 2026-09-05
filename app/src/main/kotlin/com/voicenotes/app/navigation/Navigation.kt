package com.voicenotes.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voicenotes.app.presentation.screens.HomeScreen
import com.voicenotes.app.presentation.screens.RecorderScreen
import com.voicenotes.app.presentation.screens.NoteDetailScreen
import com.voicenotes.app.presentation.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Recorder : Screen("recorder")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: String) = "note_detail/$noteId"
    }
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Recorder.route) {
            RecorderScreen(navController = navController)
        }
        composable(Screen.NoteDetail.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            NoteDetailScreen(navController = navController, noteId = noteId)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
