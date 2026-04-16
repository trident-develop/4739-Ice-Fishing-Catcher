package com.feelingtouch.r.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feelingtouch.r.ui.components.OceanButton
import com.feelingtouch.r.ui.components.OceanButtonStyle
import com.feelingtouch.r.ui.components.ScreenTitle
import com.feelingtouch.r.ui.components.UnderwaterBackground
import com.feelingtouch.r.ui.theme.AquaGlow
import com.feelingtouch.r.ui.theme.FoamWhite
import com.feelingtouch.r.ui.theme.GameFontFamily
import com.feelingtouch.r.ui.theme.OceanBlue

@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground(showFish = false, showOverlay = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ScreenTitle(text = "How To Play", fontSize = 32.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .clip(RoundedCornerShape(16.dp))
                    .background(OceanBlue.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RuleItem(
                    icon        = "\uD83D\uDC1F",
                    title       = "Tap to Reveal",
                    description = "Tap any face-down card to flip it over and reveal the fish hiding beneath."
                )
                RuleItem(
                    icon        = "\uD83D\uDD0D",
                    title       = "Find the Match",
                    description = "Tap a second card to try and find its matching fish. You can only have two cards revealed at the same time."
                )
                RuleItem(
                    icon        = "\u2705",
                    title       = "Pairs Stay Open",
                    description = "When two revealed cards show the same fish they are a match — they stay open and glow green."
                )
                RuleItem(
                    icon        = "\u274C",
                    title       = "Misses Close Back",
                    description = "If the two cards do not match they briefly stay visible, then flip face-down again. Pay attention — every peek is a clue!"
                )
                RuleItem(
                    icon        = "\uD83C\uDFAF",
                    title       = "Complete the Level",
                    description = "Find all pairs before you run out of moves to complete the level."
                )
                RuleItem(
                    icon        = "\u23F0",
                    title       = "Watch the Clock",
                    description = "Higher levels add a countdown timer. Match all pairs before time runs out or the level is failed."
                )
                RuleItem(
                    icon        = "\uD83D\uDE80",
                    title       = "Increasing Challenge",
                    description = "As levels progress the board grows larger, moves become tighter, and a timer adds extra pressure. Good luck!"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OceanButton(
                text    = "Got It!",
                onClick = onBackClick,
                style   = OceanButtonStyle.Accent,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleItem(icon: String, title: String, description: String) {
    Column {
        Text(
            text       = "$icon  $title",
            fontFamily = GameFontFamily,
            fontSize   = 18.sp,
            color      = AquaGlow
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = description,
            fontSize   = 14.sp,
            color      = FoamWhite.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}
