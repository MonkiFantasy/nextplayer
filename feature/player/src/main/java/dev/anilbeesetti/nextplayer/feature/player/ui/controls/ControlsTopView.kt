package dev.anilbeesetti.nextplayer.feature.player.ui.controls

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.extensions.copy
import dev.anilbeesetti.nextplayer.feature.player.buttons.PlayerButton

@OptIn(UnstableApi::class)
@Composable
fun ControlsTopView(
    modifier: Modifier = Modifier,
    title: String,
    onAudioClick: () -> Unit = {},
    onSubtitleClick: () -> Unit = {},
    onPlaybackSpeedClick: () -> Unit = {},
    onPlaylistClick: () -> Unit = {},
    showActions: Boolean = true,
    onBackClick: () -> Unit,
) {
    val systemBarsPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout).asPaddingValues()
    Row(
        modifier = modifier
            .padding(systemBarsPadding.copy(bottom = 0.dp))
            .padding(horizontal = 8.dp)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PlayerButton(onClick = onBackClick) {
            Canvas(modifier = Modifier.size(24.dp).padding(2.dp)) {
                val stroke = 2.2.dp.toPx()
                val startX = size.width * 0.62f
                val endX = size.width * 0.36f
                drawLine(
                    color = Color.White,
                    start = Offset(startX, size.height * 0.25f),
                    end = Offset(endX, size.height * 0.5f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Color.White,
                    start = Offset(endX, size.height * 0.5f),
                    end = Offset(startX, size.height * 0.75f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (showActions) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PlayerButton(onClick = onPlaylistClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_playlist),
                        contentDescription = null,
                    )
                }
                PlayerButton(onClick = onPlaybackSpeedClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_speed),
                        contentDescription = null,
                    )
                }
                PlayerButton(onClick = onAudioClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_audio_track),
                        contentDescription = null,
                    )
                }
                PlayerButton(onClick = onSubtitleClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_subtitle_track),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
