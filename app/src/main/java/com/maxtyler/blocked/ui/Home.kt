package com.maxtyler.blocked.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "game") {
        composable("game") {
            val vm: GameViewModel = hiltViewModel()
            GameScaffold(vm, navController = navController)
        }
        composable("settings") {
            val vm: SettingsViewModel = hiltViewModel()
            SettingsView(vm)
        }
    }
}