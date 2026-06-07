package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.common.extensions.round
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.components.NextSwitch
import dev.anilbeesetti.nextplayer.feature.player.state.rememberPlaybackParametersState
import kotlin.math.abs

private val BiliPink = Color(0xFFFF6699)
private val BiliPanel = Color(0xE6111111)
private val BiliItemSelected = Color(0x22FF6699)

@OptIn(UnstableApi::class)
@Composable
fun BoxScope.PlaybackSpeedSelectorView(
    modifier: Modifier = Modifier,
    show: Boolean,
    player: Player,
) {
    val configuration = LocalConfiguration.current
    val layoutDirection = LocalLayoutDirection.current
    val endPadding = WindowInsets.safeDrawing
        .asPaddingValues()
        .calculateEndPadding(layoutDirection)
    val isPortrait = configuration.isPortrait
    val playbackParametersState = rememberPlaybackParametersState(player)

    AnimatedVisibility(
        modifier = Modifier.align(if (isPortrait) Alignment.BottomCenter else Alignment.CenterEnd),
        visible = show,
        enter = if (isPortrait) slideInVertically { it } else slideInHorizontally { it },
        exit = if (isPortrait) slideOutVertically { it } else slideOutHorizontally { it },
    ) {
        Box(
            modifier = modifier
                .then(
                    if (isPortrait) {
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.42f)
                    } else {
                        Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                    },
                )
                .padding(end = endPadding)
                .imePadding()
                .clip(
                    if (isPortrait) {
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                    } else {
                        RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                    },
                )
                .background(BiliPanel),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isPortrait) 22.dp else 20.dp)
                    .padding(top = if (isPortrait) 18.dp else 28.dp, bottom = 22.dp),
            ) {
                Text(
                    text = "倍速",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(18.dp))

                SpeedValueControl(
                    speed = playbackParametersState.speed,
                    onDecrease = {
                        playbackParametersState.setPlaybackSpeed(
                            (playbackParametersState.speed - 0.1f).coerceAtLeast(0.2f),
                        )
                    },
                    onIncrease = {
                        playbackParametersState.setPlaybackSpeed(
                            (playbackParametersState.speed + 0.1f).coerceAtMost(4.0f),
                        )
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf(
                        4.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f,
                        1.0f, 0.75f, 0.5f, 0.2f,
                    ).forEach { speed ->
                        BiliSpeedOption(
                            text = if (abs(speed - 1.0f) < 0.01f) "1.0x 正常" else "${speed}x",
                            selected = abs(playbackParametersState.speed - speed) < 0.05f,
                            onClick = { playbackParametersState.setPlaybackSpeed(speed) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                SkipSilenceRow(
                    checked = playbackParametersState.skipSilenceEnabled,
                    onCheckedChange = { playbackParametersState.setIsSkipSilenceEnabled(it) },
                )
            }
        }
    }
}

@Composable
private fun SpeedValueControl(
    speed: Float,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpeedRoundButton(icon = R.drawable.ic_remove, onClick = onDecrease)
        Text(
            text = "${speed.round(2)}x",
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        SpeedRoundButton(icon = R.drawable.ic_add, onClick = onIncrease)
    }
}

@Composable
private fun SpeedRoundButton(
    icon: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.10f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.86f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun BiliSpeedOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) BiliItemSelected else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = if (selected) BiliPink else Color.White.copy(alpha = 0.86f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        if (selected) {
            Text(
                text = "✓",
                color = BiliPink,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SkipSilenceRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .toggleable(value = checked, onValueChange = onCheckedChange)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.skip_silence),
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
        NextSwitch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}
