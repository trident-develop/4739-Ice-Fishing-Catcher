package kr.co.company.hwa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kr.co.company.hwa.storage.PrefsManager
import kr.co.company.hwa.ui.components.OceanButton
import kr.co.company.hwa.ui.components.OceanButtonStyle
import kr.co.company.hwa.ui.components.ScreenTitle
import kr.co.company.hwa.ui.components.UnderwaterBackground
import kr.co.company.hwa.ui.theme.AquaGlow
import kr.co.company.hwa.ui.theme.FoamWhite
import kr.co.company.hwa.ui.theme.GameFontFamily
import kr.co.company.hwa.ui.theme.GoldFish
import kr.co.company.hwa.ui.theme.OceanBlue
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun LeaderboardScreen(
    prefsManager: PrefsManager,
    onBackClick: () -> Unit
) {
    val highestLevel     = remember { prefsManager.bestCompletedLevel }
    val bestScore        = remember { prefsManager.bestLevelScore }
    val totalScore       = remember { prefsManager.totalScore }
    val levelsCompleted  = remember { prefsManager.totalLevelsCompleted }
    val isInPreview      = LocalInspectionMode.current

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
            ScreenTitle(text = "Leaderboard")

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OceanBlue.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatRow(
                    label = "Highest Level",
                    value = if (highestLevel > 0) "$highestLevel" else "-"
                )

                HorizontalDivider(color = AquaGlow.copy(alpha = 0.3f))

                StatRow(
                    label = "Best Score",
                    value = if (bestScore > 0) "$bestScore" else "-"
                )

                HorizontalDivider(color = AquaGlow.copy(alpha = 0.3f))

                StatRow(
                    label = "Total Score",
                    value = if (totalScore > 0) "$totalScore" else "-"
                )

                HorizontalDivider(color = AquaGlow.copy(alpha = 0.3f))

                StatRow(
                    label = "Levels Completed",
                    value = if (levelsCompleted > 0) "$levelsCompleted" else "-"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OceanButton(
                text    = "Back",
                onClick = onBackClick,
                style   = OceanButtonStyle.Secondary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontFamily = GameFontFamily,
            fontSize   = 18.sp,
            color      = FoamWhite
        )
        Text(
            text       = value,
            fontFamily = GameFontFamily,
            fontSize   = 22.sp,
            color      = GoldFish
        )
    }
}
