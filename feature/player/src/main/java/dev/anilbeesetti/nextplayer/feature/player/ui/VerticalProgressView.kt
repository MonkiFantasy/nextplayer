package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.theme.NextPlayerTheme

private const val NORMAL_MAX_PERCENTAGE = 100

/**
 * Vertical progress indicator for volume/brightness.
 *
 * @param value Current value (0 to maxValue)
 * @param maxValue Maximum value (100 for normal, 200 for boosted volume)
 * @param icon Icon to display at the bottom
 * @param boostColor Optional color for the boost portion (above 100%)
 */
@Composable
fun VerticalProgressView(
    modifier: Modifier = Modifier,
    width: Dp = 32.dp,
    icon: Painter,
    @IntRange(from = 0, to = 200) value: Int,
    maxValue: Int = NORMAL_MAX_PERCENTAGE,
    boostColor: Color = Color(0xFFFC6E6E),
) {
    val normalizedValue = value.coerceIn(0, maxValue)
    val fillFraction = normalizedValue.toFloat() / maxValue.toFloat()
    val isBoostActive = maxValue > NORMAL_MAX_PERCENTAGE && value > NORMAL_MAX_PERCENTAGE

    Column(
        modifier = modifier
            .heightIn(max = 250.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier.size(width),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = normalizedValue.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelLarge.fontSize),
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .width(width)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight(fillFraction)
                    .background(if (isBoostActive) boostColor else MaterialTheme.colorScheme.primary),
            )
        }
        Box(
            modifier = Modifier.size(width),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
fun BiliVolumeProgressView(
    modifier: Modifier = Modifier,
    icon: Painter,
    @IntRange(from = 0, to = 200) value: Int,
    maxValue: Int = NORMAL_MAX_PERCENTAGE,
    boostColor: Color = Color(0xFFFF6B8A),
) {
    val normalizedValue = value.coerceIn(0, maxValue)
    val fillFraction = normalizedValue.toFloat() / maxValue.toFloat()
    val isBoostActive = maxValue > NORMAL_MAX_PERCENTAGE && value > NORMAL_MAX_PERCENTAGE
    val activeColor = if (isBoostActive) boostColor else Color(0xFFFB7299)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.White.copy(alpha = 0.92f),
        )

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.24f)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(activeColor),
            )
        }

        Text(
            text = normalizedValue.toString(),
            color = Color.White.copy(alpha = 0.94f),
            fontSize = 13.sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun BiliBrightnessProgressView(
    modifier: Modifier = Modifier,
    icon: Painter,
    @IntRange(from = 0, to = 100) value: Int,
    maxValue: Int = NORMAL_MAX_PERCENTAGE,
) {
    val normalizedValue = value.coerceIn(0, maxValue)
    val fillFraction = normalizedValue.toFloat() / maxValue.toFloat()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.White.copy(alpha = 0.92f),
        )

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.24f)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFB7299)),
            )
        }

        Text(
            text = "$normalizedValue%",
            color = Color.White.copy(alpha = 0.94f),
            fontSize = 13.sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview
@Composable
private fun VerticalProgressPreview() {
    NextPlayerTheme {
        VerticalProgressView(
            value = 50,
            icon = painterResource(R.drawable.ic_volume),
        )
    }
}

@Preview
@Composable
private fun VerticalProgressBoostPreview() {
    NextPlayerTheme {
        VerticalProgressView(
            value = 150,
            maxValue = 200,
            icon = painterResource(R.drawable.ic_volume),
        )
    }
}
