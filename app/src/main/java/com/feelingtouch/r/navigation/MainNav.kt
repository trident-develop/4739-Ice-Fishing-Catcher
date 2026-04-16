package com.feelingtouch.r.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.feelingtouch.r.audio.SoundManager
import com.feelingtouch.r.navigation.Routes.GAME
import com.feelingtouch.r.navigation.Routes.HOW_TO_PLAY
import com.feelingtouch.r.navigation.Routes.LEADERBOARD
import com.feelingtouch.r.navigation.Routes.LEVELS
import com.feelingtouch.r.navigation.Routes.MENU
import com.feelingtouch.r.navigation.Routes.PRIVACY_POLICY
import com.feelingtouch.r.navigation.Routes.SETTINGS
import com.feelingtouch.r.screens.GameScreen
import com.feelingtouch.r.screens.HowToPlayScreen
import com.feelingtouch.r.screens.LeaderboardScreen
import com.feelingtouch.r.screens.LevelsScreen
import com.feelingtouch.r.screens.MenuScreen
import com.feelingtouch.r.screens.PrivacyPolicyScreen
import com.feelingtouch.r.screens.SettingsScreen
import com.feelingtouch.r.storage.PrefsManager

@Composable
fun AppNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager,
    soundManager: SoundManager,
    onExitApp: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = MENU
    ) {
        composable(MENU) {
            MenuScreen(
                onPlayClick = { navController.navigate(LEVELS) },
                onSettingsClick = { navController.navigate(SETTINGS) },
                onLeaderboardClick = { navController.navigate(LEADERBOARD) },
                onExitClick = onExitApp
            )
        }

        composable(LEVELS) {
            LevelsScreen(
                prefsManager = prefsManager,
                onLevelClick = { level -> navController.navigate(Routes.game(level)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = GAME,
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameScreen(
                levelNumber = level,
                prefsManager = prefsManager,
                soundManager = soundManager,
                onBackToLevels = {
                    navController.popBackStack(LEVELS, false)
                },
                onNextLevel = { nextLevel ->
                    navController.popBackStack()
                    navController.navigate(Routes.game(nextLevel))
                },
                onRetry = {
                    navController.popBackStack()
                    navController.navigate(Routes.game(level))
                }
            )
        }

        composable(SETTINGS) {
            SettingsScreen (
                prefsManager = prefsManager,
                soundManager = soundManager,
                onHowToPlayClick = { navController.navigate(HOW_TO_PLAY) },
                onPrivacyPolicyClick = { navController.navigate(PRIVACY_POLICY) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(LEADERBOARD) {
            LeaderboardScreen(
                prefsManager = prefsManager,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(HOW_TO_PLAY) {
            HowToPlayScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}