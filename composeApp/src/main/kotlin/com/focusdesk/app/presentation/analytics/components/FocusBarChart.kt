package com.focusdesk.app.presentation.analytics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.components.GlassCard
import com.focusdesk.app.presentation.designsystem.FocusPrimary
import com.focusdesk.app.presentation.designsystem.FocusSecondary
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import com.focusdesk.domain.model.DailyFocusMetric

@Composable
fun FocusBarChart(
    metrics: List<DailyFocusMetric>,
    selectedDayIndex: Int?,
    onSelectDay: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxMinutes = (metrics.maxOfOrNull { it.focusMinutes } ?: 60).coerceAtLeast(60).toFloat()

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = 18.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Activity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (selectedDayIndex != null) {
                    val metric = metrics.getOrNull(selectedDayIndex)
                    if (metric != null) {
                        Text(
                            text = "${metric.dayLabel}: ${metric.focusMinutes}m (${metric.completedSessionsCount} sessions)",
                            style = MaterialTheme.typography.labelMedium,
                            color = FocusPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                metrics.forEachIndexed { index, metric ->
                    val isSelected = selectedDayIndex == index
                    val fraction = (metric.focusMinutes / maxMinutes).coerceIn(0.08f, 1.0f)

                    val animatedFraction by animateFloatAsState(
                        targetValue = fraction,
                        animationSpec = SwiftUiMotion.smooth(),
                        label = "bar_height_$index"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSelectDay(if (isSelected) null else index)
                            },
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .fillMaxHeight()
                            ) {
                                val barWidth = size.width
                                val barHeight = size.height * animatedFraction
                                val top = size.height - barHeight

                                drawRoundRect(
                                    color = if (isSelected) FocusSecondary else FocusPrimary,
                                    topLeft = Offset(0f, top),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = metric.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) FocusSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
