package dev.anilbeesetti.nextplayer.feature.player

import android.content.res.Configuration
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.model.ControlButtonsPosition
import dev.anilbeesetti.nextplayer.core.model.PlayerPreferences
import dev.anilbeesetti.nextplayer.core.ui.R as coreUiR
import dev.anilbeesetti.nextplayer.core.ui.extensions.copy
import dev.anilbeesetti.nextplayer.feature.player.buttons.NextButton
import dev.anilbeesetti.nextplayer.feature.player.buttons.PlayPauseButton
import dev.anilbeesetti.nextplayer.feature.player.buttons.PlayerButton
import dev.anilbeesetti.nextplayer.feature.player.buttons.PreviousButton
import dev.anilbeesetti.nextplayer.feature.player.extensions.nameRes
import dev.anilbeesetti.nextplayer.feature.player.state.ControlsVisibilityState
import dev.anilbeesetti.nextplayer.feature.player.state.VerticalGesture
import dev.anilbeesetti.nextplayer.feature.player.state.durationFormatted
import dev.anilbeesetti.nextplayer.feature.player.state.positionFormatted
import dev.anilbeesetti.nextplayer.feature.player.state.rememberBrightnessState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberControlsVisibilityState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberErrorState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberMediaPresentationState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberMetadataState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberPictureInPictureState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberRotationState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberSeekGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberTapGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberVideoZoomAndContentScaleState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberVolumeAndBrightnessGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberVolumeState
import dev.anilbeesetti.nextplayer.feature.player.state.seekToPositionFormated
import dev.anilbeesetti.nextplayer.feature.player.ui.DoubleTapIndicator
import dev.anilbeesetti.nextplayer.feature.player.ui.OverlayShowView
import dev.anilbeesetti.nextplayer.feature.player.ui.OverlayView
import dev.anilbeesetti.nextplayer.feature.player.ui.SubtitleConfiguration
import dev.anilbeesetti.nextplayer.feature.player.ui.VerticalProgressView
import dev.anilbeesetti.nextplayer.feature.player.ui.controls.ControlsBottomView
import dev.anilbeesetti.nextplayer.feature.player.ui.controls.ControlsTopView
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

val LocalControlsVisibilityState = compositionLocalOf<ControlsVisibilityState?> { null }

@OptIn(UnstableApi::class)
@Composable
fun MediaPlayerScreen(
    player: Player?,
    viewModel: PlayerViewModel,
    playerPreferences: PlayerPreferences,
    modifier: Modifier = Modifier,
    onSelectSubtitleClick: () -> Unit,
    onBackClick: () -> Unit,
    onPlayInBackgroundClick: () -> Unit,
) {
    val volumeState = rememberVolumeState(
        player = player,
        showVolumePanelIfHeadsetIsOn = playerPreferences.showSystemVolumePanel,
    )
    player ?: return
    val metadataState = rememberMetadataState(player)
    val mediaPresentationState = rememberMediaPresentationState(player)
    val controlsVisibilityState = rememberControlsVisibilityState(
        player = player,
        hideAfter = playerPreferences.controllerAutoHideTimeout.seconds,
    )
    val tapGestureState = rememberTapGestureState(
        player = player,
        doubleTapGesture = playerPreferences.doubleTapGesture,
        seekIncrementMillis = playerPreferences.seekIncrement.seconds.inWholeMilliseconds,
        useLongPressGesture = playerPreferences.useLongPressControls,
        longPressSpeed = playerPreferences.longPressControlsSpeed,
    )
    val seekGestureState = rememberSeekGestureState(
        player = player,
        sensitivity = playerPreferences.seekSensitivity,
        enableSeekGesture = playerPreferences.useSeekControls,
    )
    val pictureInPictureState = rememberPictureInPictureState(
        player = player,
        autoEnter = playerPreferences.autoPip,
    )
    val videoZoomAndContentScaleState = rememberVideoZoomAndContentScaleState(
        player = player,
        initialContentScale = playerPreferences.playerVideoZoom,
        enableZoomGesture = playerPreferences.useZoomControls,
        enablePanGesture = playerPreferences.enablePanGesture,
        onEvent = viewModel::onVideoZoomEvent,
    )
    val brightnessState = rememberBrightnessState()
    val volumeAndBrightnessGestureState = rememberVolumeAndBrightnessGestureState(
        volumeState = volumeState,
        brightnessState = brightnessState,
        enableVolumeGesture = playerPreferences.enableVolumeSwipeGesture,
        enableBrightnessGesture = playerPreferences.enableBrightnessSwipeGesture,
        volumeGestureSensitivity = playerPreferences.volumeGestureSensitivity,
        brightnessGestureSensitivity = playerPreferences.brightnessGestureSensitivity,
    )
    val rotationState = rememberRotationState(
        player = player,
        screenOrientation = playerPreferences.playerScreenOrientation,
    )
    val errorState = rememberErrorState(player = player)
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    LaunchedEffect(pictureInPictureState.isInPictureInPictureMode) {
        if (pictureInPictureState.isInPictureInPictureMode) {
            controlsVisibilityState.hideControls()
        }
    }

    LaunchedEffect(tapGestureState.isLongPressGestureInAction) {
        if (tapGestureState.isLongPressGestureInAction) {
            controlsVisibilityState.hideControls()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (playerPreferences.rememberPlayerBrightness) {
            brightnessState.setBrightness(playerPreferences.playerBrightness)
        }
    }

    LaunchedEffect(brightnessState.currentBrightness) {
        if (playerPreferences.rememberPlayerBrightness) {
            viewModel.updatePlayerBrightness(brightnessState.currentBrightness)
        }
    }

    var overlayView by remember { mutableStateOf<OverlayView?>(null) }

    CompositionLocalProvider(LocalControlsVisibilityState provides controlsVisibilityState) {
        Box {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                PlayerContentFrame(
                    modifier = if (isPortrait) {
                        Modifier.offset(y = (-72).dp)
                    } else {
                        Modifier
                    },
                    player = player,
                    pictureInPictureState = pictureInPictureState,
                    controlsVisibilityState = controlsVisibilityState,
                    tapGestureState = tapGestureState,
                    seekGestureState = seekGestureState,
                    videoZoomAndContentScaleState = videoZoomAndContentScaleState,
                    volumeAndBrightnessGestureState = volumeAndBrightnessGestureState,
                    subtitleConfiguration = SubtitleConfiguration(
                        useSystemCaptionStyle = playerPreferences.useSystemCaptionStyle,
                        showBackground = playerPreferences.subtitleBackground,
                        font = playerPreferences.subtitleFont,
                        textSize = playerPreferences.subtitleTextSize,
                        textBold = playerPreferences.subtitleTextBold,
                        applyEmbeddedStyles = playerPreferences.applyEmbeddedStyles,
                    ),
                )

                AnimatedVisibility(
                    visible = controlsVisibilityState.controlsVisible && !controlsVisibilityState.controlsLocked,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                    )
                }

                if (mediaPresentationState.isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp),
                    )
                }

                DoubleTapIndicator(tapGestureState = tapGestureState)

                AnimatedVisibility(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .align(Alignment.TopCenter),
                    visible = tapGestureState.isLongPressGestureInAction,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    BiliLongPressSpeedIndicator(
                        text = stringResource(coreUiR.string.fast_playback_speed, tapGestureState.longPressSpeed),
                    )
                }

                if (controlsVisibilityState.controlsVisible && controlsVisibilityState.controlsLocked) {
                    BiliLockedControls(
                        onUnlockClick = { controlsVisibilityState.unlockControls() },
                    )
                } else {
                    PlayerControlsView(
                        topView = {
                            AnimatedVisibility(
                                visible = controlsVisibilityState.controlsVisible,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                ControlsTopView(
                                    title = (metadataState.title ?: "").takeUnless { isPortrait } ?: "",
                                    onAudioClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.AUDIO_SELECTOR
                                    },
                                    onSubtitleClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.SUBTITLE_SELECTOR
                                    },
                                    onPlaybackSpeedClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYBACK_SPEED
                                    },
                                    onPlaylistClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYLIST
                                    },
                                    showActions = false,
                                    onBackClick = onBackClick,
                                )
                            }
                        },
                        middleView = {
                            if (controlsVisibilityState.controlsVisible) {
                                BiliPortraitCornerActions(
                                    onPictureInPictureClick = if (pictureInPictureState.isPipSupported) {
                                        {
                                            if (!pictureInPictureState.hasPipPermission) {
                                                Toast.makeText(
                                                    context,
                                                    coreUiR.string.enable_pip_from_settings,
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                                pictureInPictureState.openPictureInPictureSettings()
                                            } else {
                                                pictureInPictureState.enterPictureInPictureMode()
                                            }
                                        }
                                    } else {
                                        null
                                    },
                                    onPlayInBackgroundClick = onPlayInBackgroundClick,
                                )
                                if (!isPortrait) {
                                    BiliCenterRightLockButton(
                                        onClick = {
                                            controlsVisibilityState.showControls()
                                            controlsVisibilityState.lockControls()
                                        },
                                    )
                                }
                            }
                            if (controlsVisibilityState.controlsVisible && isPortrait) {
                                BiliPortraitSideActions(
                                    onSubtitleClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.SUBTITLE_SELECTOR
                                    },
                                    onPlaylistClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYLIST
                                    },
                                    onPlaybackSpeedClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYBACK_SPEED
                                    },
                                    onVideoScaleClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.VIDEO_CONTENT_SCALE
                                    },
                                    onLockControlsClick = {
                                        controlsVisibilityState.showControls()
                                        controlsVisibilityState.lockControls()
                                    },
                                )
                            }
                            when {
                                seekGestureState.seekAmount != null -> BiliSeekPreview(
                                    position = seekGestureState.seekToPositionFormated,
                                    duration = mediaPresentationState.durationFormatted,
                                )
                                videoZoomAndContentScaleState.isZooming -> InfoView(
                                    info = "${(videoZoomAndContentScaleState.zoom * 100).toInt()}%",
                                )
                                videoZoomAndContentScaleState.showContentScaleIndicator -> InfoView(
                                    info = stringResource(videoZoomAndContentScaleState.videoContentScale.nameRes()),
                                )
                                controlsVisibilityState.controlsVisible &&
                                    isPortrait &&
                                    !mediaPresentationState.isPlaying -> BiliPauseCenterButton(
                                    player = player,
                                    timeText = "${mediaPresentationState.positionFormatted} / ${mediaPresentationState.durationFormatted}",
                                )
                                else -> Unit
                            }
                        },
                        bottomView = {
                            AnimatedVisibility(
                                visible = controlsVisibilityState.controlsVisible && !controlsVisibilityState.controlsLocked,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                ControlsBottomView(
                                    player = player,
                                    mediaPresentationState = mediaPresentationState,
                                    title = metadataState.title ?: "",
                                    controlsAlignment = when (playerPreferences.controlButtonsPosition) {
                                        ControlButtonsPosition.LEFT -> Alignment.Start
                                        ControlButtonsPosition.RIGHT -> Alignment.End
                                    },
                                    videoContentScale = videoZoomAndContentScaleState.videoContentScale,
                                    isPipSupported = pictureInPictureState.isPipSupported,
                                    onSeek = seekGestureState::onSeek,
                                    onSeekEnd = seekGestureState::onSeekEnd,
                                    onRotateClick = rotationState::rotate,
                                    onPlayInBackgroundClick = onPlayInBackgroundClick,
                                    onSubtitleClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.SUBTITLE_SELECTOR
                                    },
                                    onPlaylistClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYLIST
                                    },
                                    onPlaybackSpeedClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.PLAYBACK_SPEED
                                    },
                                    onLockControlsClick = {
                                        controlsVisibilityState.showControls()
                                        controlsVisibilityState.lockControls()
                                    },
                                    onVideoContentScaleClick = {
                                        controlsVisibilityState.showControls()
                                        videoZoomAndContentScaleState.switchToNextVideoContentScale()
                                    },
                                    onVideoContentScaleLongClick = {
                                        controlsVisibilityState.hideControls()
                                        overlayView = OverlayView.VIDEO_CONTENT_SCALE
                                    },
                                    onPictureInPictureClick = {
                                        if (!pictureInPictureState.hasPipPermission) {
                                            Toast.makeText(
                                                context,
                                                coreUiR.string.enable_pip_from_settings,
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            pictureInPictureState.openPictureInPictureSettings()
                                        } else {
                                            pictureInPictureState.enterPictureInPictureMode()
                                        }
                                    },
                                )
                            }
                        },
                    )
                }

                val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .displayCutoutPadding()
                        .padding(systemBarsPadding.copy(top = 0.dp, bottom = 0.dp))
                        .padding(24.dp),
                ) {
                    AnimatedVisibility(
                        modifier = Modifier.align(Alignment.CenterStart),
                        visible = volumeAndBrightnessGestureState.activeGesture == VerticalGesture.VOLUME,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        VerticalProgressView(
                            value = volumeState.volumePercentage,
                            maxValue = volumeState.maxVolumePercentage,
                            icon = painterResource(coreUiR.drawable.ic_volume),
                        )
                    }

                    AnimatedVisibility(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        visible = volumeAndBrightnessGestureState.activeGesture == VerticalGesture.BRIGHTNESS,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        VerticalProgressView(
                            value = brightnessState.brightnessPercentage,
                            icon = painterResource(coreUiR.drawable.ic_brightness),
                        )
                    }
                }
            }

            OverlayShowView(
                player = player,
                overlayView = overlayView,
                videoContentScale = videoZoomAndContentScaleState.videoContentScale,
                onDismiss = { overlayView = null },
                onSelectSubtitleClick = onSelectSubtitleClick,
                onSubtitleOptionEvent = viewModel::onSubtitleOptionEvent,
                onVideoContentScaleChanged = { videoZoomAndContentScaleState.onVideoContentScaleChanged(it) },
            )
        }
    }

    errorState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = stringResource(coreUiR.string.error_playing_video))
            },
            text = {
                Text(text = error.message ?: stringResource(coreUiR.string.unknown_error))
            },
            confirmButton = {
                if (player.hasNextMediaItem()) {
                    TextButton(
                        onClick = {
                            errorState.dismiss()
                            player.seekToNext()
                            player.play()
                        },
                    ) {
                        Text(text = stringResource(coreUiR.string.play_next_video))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        errorState.dismiss()
                        onBackClick()
                    },
                ) {
                    Text(text = stringResource(coreUiR.string.exit))
                }
            },
        )
    }

    BackHandler {
        if (overlayView != null) {
            overlayView = null
        } else {
            onBackClick()
        }
    }
}

@Composable
fun BoxScope.BiliPauseCenterButton(player: Player, timeText: String) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.28f)),
            contentAlignment = Alignment.Center,
        ) {
            PlayPauseButton(player = player, modifier = Modifier.size(60.dp))
        }
        Text(
            text = timeText,
            color = Color.White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun BoxScope.BiliPortraitSideActions(
    onSubtitleClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onPlaybackSpeedClick: () -> Unit,
    onVideoScaleClick: () -> Unit,
    onLockControlsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 18.dp, bottom = 132.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BiliPortraitSideAction(label = "字幕", icon = coreUiR.drawable.ic_subtitle_track, onClick = onSubtitleClick)
        BiliPortraitSideAction(label = "选集", icon = coreUiR.drawable.ic_playlist, onClick = onPlaylistClick)
        BiliPortraitSideAction(label = "锁定", icon = coreUiR.drawable.ic_lock_open, onClick = onLockControlsClick)
        BiliPortraitSideAction(label = "倍速", icon = coreUiR.drawable.ic_speed, onClick = onPlaybackSpeedClick)
        BiliPortraitSideAction(label = "缩放", icon = coreUiR.drawable.ic_width_wide, onClick = onVideoScaleClick)
    }
}

@Composable
private fun BoxScope.BiliCenterRightLockButton(onClick: () -> Unit) {
    BiliPortraitSideAction(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 18.dp),
        label = "锁定",
        icon = coreUiR.drawable.ic_lock_open,
        onClick = onClick,
    )
}

@Composable
fun BoxScope.BiliLockedControls(onUnlockClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 18.dp),
    ) {
        BiliUnlockButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onUnlockClick,
        )
        BiliUnlockButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onUnlockClick,
        )
    }
}

@Composable
private fun BiliUnlockButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    PlayerButton(
        modifier = modifier.size(42.dp),
        containerColor = Color.Black.copy(0.36f),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(coreUiR.drawable.ic_lock),
            contentDescription = stringResource(coreUiR.string.controls_unlock),
            modifier = Modifier.size(24.dp),
            tint = Color.White,
        )
    }
}

@Composable
private fun BoxScope.BiliPortraitCornerActions(
    onPictureInPictureClick: (() -> Unit)?,
    onPlayInBackgroundClick: () -> Unit,
) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = systemBarsPadding.calculateTopPadding(), end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onPictureInPictureClick?.let {
            BiliPortraitSmallAction(label = "小窗", icon = coreUiR.drawable.ic_pip, onClick = it)
        }
        BiliPortraitSmallAction(label = "后台", icon = coreUiR.drawable.ic_headset, onClick = onPlayInBackgroundClick)
    }
}

@Composable
private fun BiliPortraitSmallAction(
    label: String,
    icon: Int,
    onClick: () -> Unit,
) {
    PlayerButton(
        modifier = Modifier.size(34.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(22.dp),
            tint = Color.White,
        )
    }
}

@Composable
private fun BiliPortraitSideAction(
    modifier: Modifier = Modifier,
    label: String,
    icon: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        PlayerButton(
            modifier = Modifier.size(36.dp),
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        }
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun BiliSeekPreview(
    modifier: Modifier = Modifier,
    position: String,
    duration: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 230.dp, height = 128.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "预览",
                color = Color.White.copy(alpha = 0.58f),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Text(
            text = "$position / $duration",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
fun BiliLongPressSpeedIndicator(
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFF4F5865).copy(alpha = 0.66f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        BiliAnimatedFastForwardTriangles()
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun BiliAnimatedFastForwardTriangles(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bili-fast-forward")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 960, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bili-fast-forward-progress",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val phase = ((progress.value * 3f) - index).floorMod(3f)
            val alpha = (1f - (abs(phase - 1f) / 1f)).coerceIn(0.24f, 1f)
            Canvas(
                modifier = Modifier
                    .size(width = 13.dp, height = 15.dp)
                    .blur(((1f - alpha) * 2.2f).dp),
            ) {
                val path = AndroidPath().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(0f, size.height)
                    close()
                }
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb((alpha * 255).roundToInt(), 255, 255, 255)
                    style = Paint.Style.FILL
                    pathEffect = CornerPathEffect(1.8.dp.toPx())
                }
                drawContext.canvas.nativeCanvas.drawPath(path, paint)
            }
        }
    }
}

private fun Float.floorMod(other: Float): Float = ((this % other) + other) % other

@Composable
fun InfoView(
    modifier: Modifier = Modifier,
    info: String,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = info,
            style = textStyle,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ControlsMiddleView(modifier: Modifier = Modifier, player: Player) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(40.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PreviousButton(player = player)
        PlayPauseButton(player = player)
        NextButton(player = player)
    }
}

@Composable
fun PlayerControlsView(
    modifier: Modifier = Modifier,
    topView: @Composable () -> Unit,
    middleView: @Composable BoxScope.() -> Unit,
    bottomView: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            topView()
            Spacer(modifier = Modifier.weight(1f))
            bottomView()
        }

        middleView()
    }
}
