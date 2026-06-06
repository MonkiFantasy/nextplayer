package dev.anilbeesetti.nextplayer.feature.player.ui.controls

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
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
import dev.anilbeesetti.nextplayer.feature.player.buttons.PreviousButton
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
    seekbarPosition: Long = mediaPresentationState.position,
    title: String,
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
    val configuration = LocalConfiguration.current
    val systemBarsPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout).asPaddingValues()
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier = modifier
            .padding(systemBarsPadding.copy(top = 0.dp))
            .padding(horizontal = if (isPortrait) 20.dp else 22.dp)
            .padding(bottom = 14.dp.takeIf { systemBarsPadding.calculateBottomPadding() == 0.dp } ?: 0.dp),
        verticalArrangement = Arrangement.spacedBy(if (isPortrait) 6.dp else 8.dp),
    ) {
        if (isPortrait) {
            BiliPortraitBottomControls(
                player = player,
                mediaPresentationState = mediaPresentationState,
                seekbarPosition = seekbarPosition,
                title = title,
                videoContentScale = videoContentScale,
                isPipSupported = isPipSupported,
                onVideoContentScaleClick = onVideoContentScaleClick,
                onVideoContentScaleLongClick = onVideoContentScaleLongClick,
                onLockControlsClick = onLockControlsClick,
                onPictureInPictureClick = onPictureInPictureClick,
                onRotateClick = onRotateClick,
                onPlayInBackgroundClick = onPlayInBackgroundClick,
                onSubtitleClick = onSubtitleClick,
                onPlaylistClick = onPlaylistClick,
                onPlaybackSpeedClick = onPlaybackSpeedClick,
                onSeek = onSeek,
                onSeekEnd = onSeekEnd,
            )
        } else {
            BiliLandscapeBottomControls(
                player = player,
                mediaPresentationState = mediaPresentationState,
                seekbarPosition = seekbarPosition,
                controlsAlignment = controlsAlignment,
                videoContentScale = videoContentScale,
                isPipSupported = isPipSupported,
                onVideoContentScaleClick = onVideoContentScaleClick,
                onVideoContentScaleLongClick = onVideoContentScaleLongClick,
                onLockControlsClick = onLockControlsClick,
                onPictureInPictureClick = onPictureInPictureClick,
                onRotateClick = onRotateClick,
                onPlayInBackgroundClick = onPlayInBackgroundClick,
                onSubtitleClick = onSubtitleClick,
                onPlaylistClick = onPlaylistClick,
                onPlaybackSpeedClick = onPlaybackSpeedClick,
                onSeek = onSeek,
                onSeekEnd = onSeekEnd,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun BiliLandscapeBottomControls(
    player: Player,
    mediaPresentationState: MediaPresentationState,
    seekbarPosition: Long,
    controlsAlignment: Alignment.Horizontal,
    videoContentScale: VideoContentScale,
    isPipSupported: Boolean,
    onVideoContentScaleClick: () -> Unit,
    onVideoContentScaleLongClick: () -> Unit,
    onLockControlsClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onRotateClick: () -> Unit,
    onPlayInBackgroundClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onPlaybackSpeedClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekEnd: () -> Unit,
) {
    Text(
        text = "${mediaPresentationState.positionFormatted}/${mediaPresentationState.durationFormatted}",
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
    )
    BiliPlayerSeekbar(
        position = seekbarPosition.toFloat(),
        duration = mediaPresentationState.duration.toFloat(),
        enabled = mediaPresentationState.duration > 0L && player.isCurrentMediaItemSeekable,
        onSeek = { onSeek(it.toLong()) },
        onSeekFinished = onSeekEnd,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlayPauseButton(player = player, modifier = Modifier.size(44.dp))
        PreviousButton(player = player, modifier = Modifier.size(34.dp))
        NextButton(player = player, modifier = Modifier.size(34.dp))
        Spacer(modifier = Modifier.weight(1f))
        BiliActionRow(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            controlsAlignment = controlsAlignment,
            videoContentScale = videoContentScale,
            isPipSupported = isPipSupported,
            onVideoContentScaleClick = onVideoContentScaleClick,
            onVideoContentScaleLongClick = onVideoContentScaleLongClick,
            onLockControlsClick = onLockControlsClick,
            onPictureInPictureClick = onPictureInPictureClick,
            onRotateClick = onRotateClick,
            onPlayInBackgroundClick = onPlayInBackgroundClick,
            onSubtitleClick = onSubtitleClick,
            onPlaylistClick = onPlaylistClick,
            onPlaybackSpeedClick = onPlaybackSpeedClick,
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun BiliPortraitBottomControls(
    player: Player,
    mediaPresentationState: MediaPresentationState,
    seekbarPosition: Long,
    title: String,
    videoContentScale: VideoContentScale,
    isPipSupported: Boolean,
    onVideoContentScaleClick: () -> Unit,
    onVideoContentScaleLongClick: () -> Unit,
    onLockControlsClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onRotateClick: () -> Unit,
    onPlayInBackgroundClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onPlaybackSpeedClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekEnd: () -> Unit,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
    )
    BiliPlayerSeekbar(
        position = seekbarPosition.toFloat(),
        duration = mediaPresentationState.duration.toFloat(),
        enabled = mediaPresentationState.duration > 0L && player.isCurrentMediaItemSeekable,
        onSeek = { onSeek(it.toLong()) },
        onSeekFinished = onSeekEnd,
        compact = true,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PreviousButton(player = player, modifier = Modifier.size(32.dp))
        NextButton(player = player, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.weight(1f))
        BiliFullscreenButton(onClick = onRotateClick)
    }
}

@Composable
private fun BiliActionRow(
    modifier: Modifier = Modifier,
    controlsAlignment: Alignment.Horizontal,
    videoContentScale: VideoContentScale,
    isPipSupported: Boolean,
    onVideoContentScaleClick: () -> Unit,
    onVideoContentScaleLongClick: () -> Unit,
    onLockControlsClick: () -> Unit,
    onPictureInPictureClick: () -> Unit,
    onRotateClick: () -> Unit,
    onPlayInBackgroundClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onPlaybackSpeedClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp, alignment = controlsAlignment),
    ) {
        BiliTextAction(text = "字幕", onClick = onSubtitleClick)
        BiliTextAction(text = "选集", onClick = onPlaylistClick)
        BiliTextAction(text = "倍速", onClick = onPlaybackSpeedClick)
        BiliTextAction(text = "缩放", onClick = onVideoContentScaleLongClick)
        BiliIconAction(onClick = onRotateClick, icon = R.drawable.ic_screen_rotation)
    }
}

@Composable
private fun BiliTextAction(text: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier.clickable(onClick = onClick),
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun BiliIconAction(onClick: () -> Unit, icon: Int) {
    PlayerButton(
        modifier = Modifier.size(34.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun BiliFullscreenButton(onClick: () -> Unit) {
    PlayerButton(
        modifier = Modifier.size(34.dp),
        onClick = onClick,
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val stroke = 2.dp.toPx()
            val arm = 7.dp.toPx()
            val pad = 3.dp.toPx()
            drawLine(
                Color.White,
                Offset(pad, pad),
                Offset(pad + arm, pad),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                Color.White,
                Offset(pad, pad),
                Offset(pad, pad + arm),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                Color.White,
                Offset(size.width - pad, size.height - pad),
                Offset(size.width - pad - arm, size.height - pad),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                Color.White,
                Offset(size.width - pad, size.height - pad),
                Offset(size.width - pad, size.height - pad - arm),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BiliPlayerSeekbar(
    modifier: Modifier = Modifier,
    position: Float,
    duration: Float,
    enabled: Boolean = duration > 0f,
    compact: Boolean = false,
    onSeek: (Float) -> Unit,
    onSeekFinished: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val trackHeight = if (compact) 2.4.dp else 3.dp
    val safeDuration = duration.coerceAtLeast(0f)
    val seekEnabled = enabled && safeDuration > 0f
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Slider(
            modifier = modifier
                .fillMaxWidth()
                .height(if (compact) 24.dp else 26.dp),
            value = position.coerceIn(0f, safeDuration),
            valueRange = 0f..safeDuration.coerceAtLeast(1f),
            onValueChange = {
                if (seekEnabled) {
                    onSeek(it)
                }
            },
            onValueChangeFinished = {
                if (seekEnabled) {
                    onSeekFinished()
                }
            },
            enabled = seekEnabled,
            interactionSource = interactionSource,
            track = { sliderState ->
                val min = sliderState.valueRange.start
                val max = sliderState.valueRange.endInclusive
                val range = (max - min).takeIf { it > 0f } ?: 1f
                val playedFraction = ((sliderState.value - min) / range).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
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
                        .width(if (compact) 18.dp else 20.dp)
                        .height(if (compact) 15.dp else 16.dp)
                        .clip(RoundedCornerShape(4.5.dp))
                        .background(Color.White)
                        .border(1.4.dp, Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.5.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(if (compact) 8.dp else 9.dp)) {
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.88f),
                            radius = 1.55.dp.toPx(),
                            center = center.copy(x = center.x - 2.3.dp.toPx()),
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.88f),
                            radius = 1.55.dp.toPx(),
                            center = center.copy(x = center.x + 2.3.dp.toPx()),
                        )
                    }
                }
            },
        )
    }
}
