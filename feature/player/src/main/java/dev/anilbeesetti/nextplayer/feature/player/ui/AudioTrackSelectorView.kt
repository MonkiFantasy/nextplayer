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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.feature.player.extensions.getName
import dev.anilbeesetti.nextplayer.feature.player.extensions.noRippleClickable
import dev.anilbeesetti.nextplayer.feature.player.state.rememberTracksState

private val BiliBlue = Color(0xFF00A3FF)
private val BiliSelectedBlue = Color(0x2200A3FF)

@OptIn(UnstableApi::class)
@Composable
fun BoxScope.AudioTrackSelectorView(
    modifier: Modifier = Modifier,
    show: Boolean,
    player: Player,
    onDismiss: () -> Unit,
) {
    val audioTracksState = rememberTracksState(player, C.TRACK_TYPE_AUDIO)
    val items = audioTracksState.tracks.mapIndexed { index, track ->
        BiliAudioTrackItem(
            title = track.mediaTrackGroup.getName(C.TRACK_TYPE_AUDIO, index),
            selected = track.isSelected,
            onClick = {
                audioTracksState.switchTrack(index)
                onDismiss()
            },
        )
    } + BiliAudioTrackItem(
        title = stringResource(R.string.disable),
        selected = audioTracksState.tracks.none { it.isSelected },
        onClick = {
            audioTracksState.switchTrack(-1)
            onDismiss()
        },
    )

    BiliAudioOverlay(
        modifier = modifier,
        show = show,
        itemCount = audioTracksState.tracks.size,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp)
                .padding(top = 4.dp, bottom = 18.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items.forEach { item ->
                BiliTrackRow(
                    text = item.title,
                    selected = item.selected,
                    onClick = item.onClick,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.BiliAudioOverlay(
    modifier: Modifier = Modifier,
    show: Boolean,
    itemCount: Int,
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val layoutDirection = LocalLayoutDirection.current
    val endPadding = WindowInsets.safeDrawing
        .asPaddingValues()
        .calculateEndPadding(layoutDirection)
    val isPortrait = configuration.isPortrait
    val panelShape = if (isPortrait) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    } else {
        RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp)
    }

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
                            .fillMaxHeight(0.40f)
                    } else {
                        Modifier
                            .fillMaxWidth(0.34f)
                            .fillMaxHeight(0.88f)
                    },
                )
                .padding(end = endPadding)
                .imePadding()
                .clip(panelShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF21B1B22),
                            Color(0xF014141A),
                        ),
                    ),
                )
                .noRippleClickable { },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "音轨",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (itemCount > 0) {
                        Text(
                            text = "$itemCount 个音轨",
                            color = Color.White.copy(alpha = 0.48f),
                            fontSize = 12.sp,
                        )
                    }
                }
                content()
            }
        }
    }
}

@Composable
private fun BiliTrackRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) BiliSelectedBlue else Color.White.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = if (selected) BiliBlue else Color.White.copy(alpha = 0.86f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            Text(
                text = "✓",
                color = BiliBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private data class BiliAudioTrackItem(
    val title: String,
    val selected: Boolean,
    val onClick: () -> Unit,
)
