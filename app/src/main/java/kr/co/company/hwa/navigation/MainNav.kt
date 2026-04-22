package kr.co.company.hwa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kr.co.company.hwa.audio.SoundManager
import kr.co.company.hwa.navigation.Routes.GAME
import kr.co.company.hwa.navigation.Routes.HOW_TO_PLAY
import kr.co.company.hwa.navigation.Routes.LEADERBOARD
import kr.co.company.hwa.navigation.Routes.LEVELS
import kr.co.company.hwa.navigation.Routes.MENU
import kr.co.company.hwa.navigation.Routes.PRIVACY_POLICY
import kr.co.company.hwa.navigation.Routes.SETTINGS
import kr.co.company.hwa.screens.GameScreen
import kr.co.company.hwa.screens.HowToPlayScreen
import kr.co.company.hwa.screens.LeaderboardScreen
import kr.co.company.hwa.screens.LevelsScreen
import kr.co.company.hwa.screens.MenuScreen
import kr.co.company.hwa.screens.PrivacyPolicyScreen
import kr.co.company.hwa.screens.SettingsScreen
import kr.co.company.hwa.storage.PrefsManager

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