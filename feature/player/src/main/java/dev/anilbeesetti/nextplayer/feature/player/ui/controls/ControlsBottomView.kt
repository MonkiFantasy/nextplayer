package dev.anilbeesetti.nextplayer.feature.player.ui.controls

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.model.VideoContentScale
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.extensions.copy
import dev.anilbeesetti.nextplayer.feature.player.buttons.NextButton
import dev.anilbeesetti.nextplayer.feature.player.buttons.PlayPauseButton
import dev.anilbeesetti.nextplayer.feature.player.buttons.PlayerButton
import dev.anilbeesetti.nextplayer.feature.player.extensions.drawableRes
import dev.anilbeesetti.nextplayer.feature.player.state.MediaPresentationState
import dev.anilbeesetti.nextplayer.feature.player.state.durationFormatted
import dev.anilbeesetti.nextplayer.feature.player.state.positionFormatted

private val BiliPink = Color(0xFFFF6699)

@OptIn(UnstableApi::class)
@Composable
fun ControlsBottomView(
    modifier: Modifier = Modifier,
    player: Player,
    mediaPresentationState: MediaPresentationState,
    controlsAlignment: Alignment.Horizontal,
    videoContentScale: VideoContentScale,
    isPipSupported: Boolean,
    onVideoContentScaleClick: () -> Unit,
    onVideoContentScaleLongClick: () -> Unit,
    onLockControlsClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onRotateClick: () -> Unit,
    onPlayInBackgroundClick: () -> Unit,
    onSubtitleClick: () -> Unit = {},
    onPlaylistClick: () -> Unit = {},
    onPlaybackSpeedClick: () -> Unit = {},
    onSeek: (Long) -> Unit,
    onSeekEnd: () -> Unit,
) {
    val systemBarsPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout).asPaddingValues()
    Column(
        modifier = modifier
            .padding(systemBarsPadding.copy(top = 0.dp))
            .padding(horizontal = 24.dp)
            .padding(bottom = 18.dp.takeIf { systemBarsPadding.calculateBottomPadding() == 0.dp } ?: 0.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "${mediaPresentationState.positionFormatted}/${mediaPresentationState.durationFormatted}",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )

        BiliPlayerSeekbar(
            position = mediaPresentationState.position.toFloat(),
            duration = mediaPresentationState.duration.toFloat(),
            onSeek = { onSeek(it.toLong()) },
            onSeekFinished = onSeekEnd,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PlayPauseButton(player = player, modifier = Modifier.size(58.dp))
            NextButton(player = player, modifier = Modifier.size(42.dp))
            BiliDanmakuButton(text = "弹")
            BiliDanmakuButton(text = "弹⚙")

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.88f))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "发个友善的弹幕见证当下",
                    color = Color.Black.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = controlsAlignment),
            ) {
                BiliTextAction(text = "字幕", onClick = onSubtitleClick)
                BiliTextAction(text = "选集", onClick = onPlaylistClick)
                BiliTextAction(text = "倍速", onClick = onPlaybackSpeedClick)
                BiliTextAction(text = "自动", onClick = onPlayInBackgroundClick)
                PlayerButton(onClick = onLockControlsClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock_open),
                        contentDescription = null,
                    )
                }
                PlayerButton(
                    onClick = onVideoContentScaleClick,
                    onLongClick = onVideoContentScaleLongClick,
                ) {
                    Icon(
                        painter = painterResource(videoContentScale.drawableRes()),
                        contentDescription = null,
                    )
                }
                if (isPipSupported) {
                    PlayerButton(onClick = onPictureInPictureClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pip),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BiliDanmakuButton(text: String) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, Color.White, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BiliTextAction(text: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier.clickable(onClick = onClick),
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Medium,
    )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BiliPlayerSeekbar(
    modifier: Modifier = Modifier,
    position: Float,
    duration: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Slider(
            modifier = modifier
                .fillMaxWidth()
                .height(28.dp),
            value = position.coerceIn(0f, duration.coerceAtLeast(0f)),
            valueRange = 0f..duration.coerceAtLeast(1f),
            onValueChange = onSeek,
            onValueChangeFinished = onSeekFinished,
            interactionSource = interactionSource,
            track = { sliderState ->
                val min = sliderState.valueRange.start
                val max = sliderState.valueRange.endInclusive
                val range = (max - min).takeIf { it > 0f } ?: 1f
                val playedFraction = ((sliderState.value - min) / range).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.34f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(playedFraction)
                            .background(BiliPink),
                    )
                }
            },
            thumb = {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color.Black.copy(alpha = 0.75f), RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.88f),
                            radius = 2.2.dp.toPx(),
                            center = center.copy(x = center.x - 3.dp.toPx()),
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.88f),
                            radius = 2.2.dp.toPx(),
                            center = center.copy(x = center.x + 3.dp.toPx()),
                        )
                    }
                }
            },
        )
    }
}
