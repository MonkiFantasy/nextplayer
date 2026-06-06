package dev.anilbeesetti.nextplayer.feature.player.state

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.feature.player.extensions.formatted
import dev.anilbeesetti.nextplayer.feature.player.extensions.setIsScrubbingModeEnabled
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@Composable
fun rememberSeekGestureState(
    player: Player,
    sensitivity: Float = 0.5f,
    enableSeekGesture: Boolean,
): SeekGestureState {
    val seekGestureState = remember {
        SeekGestureState(
            player = player,
            sensitivity = sensitivity,
            enableSeekGesture = enableSeekGesture,
        )
    }
    return seekGestureState
}

@Stable
class SeekGestureState(
    private val player: Player,
    private val enableSeekGesture: Boolean = true,
    private val sensitivity: Float = 0.5f,
) {
    var isSeeking: Boolean by mutableStateOf(false)
        private set

    var seekStartPosition: Long? by mutableStateOf(null)
        private set

    var seekAmount: Long? by mutableStateOf(null)
        private set

    private var seekStartX = 0f

    fun onSeek(value: Long) {
        val duration = validSeekDurationOrNull() ?: run {
            if (isSeeking) reset()
            return
        }

        if (!isSeeking) {
            isSeeking = true
            seekStartPosition = player.currentPosition.coerceIn(0L, duration)
            player.setIsScrubbingModeEnabled(true)
        }

        val startPosition = seekStartPosition ?: player.currentPosition.coerceIn(0L, duration)
        val targetPosition = value.coerceIn(0L, duration)
        seekAmount = (targetPosition - startPosition).coerceIn(
            minimumValue = 0L - startPosition,
            maximumValue = duration - startPosition,
        )
    }

    fun onSeekEnd() {
        commitSeekAndReset()
    }

    fun onDragStart(offset: Offset) {
        if (!enableSeekGesture) return
        val duration = validSeekDurationOrNull() ?: return

        isSeeking = true
        seekStartX = offset.x
        seekStartPosition = player.currentPosition.coerceIn(0L, duration)

        player.setIsScrubbingModeEnabled(true)
    }

    @OptIn(UnstableApi::class)
    fun onDrag(change: PointerInputChange, dragAmount: Float) {
        val startPosition = seekStartPosition ?: return
        val duration = validSeekDurationOrNull() ?: return
        if (change.isConsumed) return

        val newPosition = startPosition + ((change.position.x - seekStartX) * (sensitivity * 100)).toInt()
        seekAmount = (newPosition - startPosition).coerceIn(
            minimumValue = 0 - startPosition,
            maximumValue = duration - startPosition,
        )
    }

    fun onDragEnd() {
        commitSeekAndReset()
    }

    private fun commitSeekAndReset() {
        seekTargetPositionOrNull()?.let(player::seekTo)
        reset()
    }

    private fun seekTargetPositionOrNull(): Long? {
        val duration = validSeekDurationOrNull() ?: return null
        val startPosition = seekStartPosition ?: return null
        val amount = seekAmount ?: return null
        return (startPosition + amount).coerceIn(0L, duration)
    }

    private fun reset() {
        player.setIsScrubbingModeEnabled(false)
        isSeeking = false
        seekStartPosition = null
        seekAmount = null

        seekStartX = 0f
    }

    private fun validSeekDurationOrNull(): Long? {
        val duration = player.duration
        return duration.takeIf {
            it != C.TIME_UNSET && it > 0L && player.isCurrentMediaItemSeekable
        }
    }
}

val SeekGestureState.seekAmountFormatted: String
    get() {
        val seekAmount = seekAmount ?: return ""
        val sign = if (seekAmount < 0) "-" else "+"
        return sign + abs(seekAmount).milliseconds.formatted()
    }

val SeekGestureState.seekToPositionFormated: String
    get() {
        val position = seekStartPosition ?: return ""
        val seekAmount = seekAmount ?: return ""
        return (position + seekAmount).milliseconds.formatted()
    }
