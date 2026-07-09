package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintGreen

@Composable
fun MilkChart(
    averageYield: Double,
    modifier: Modifier = Modifier
) {
    // Generate a weekly milk yield simulation based on average yield (e.g., peak on day 4)
    val dailyData = remember(averageYield) {
        listOf(
            averageYield - 1.2,
            averageYield - 0.5,
            averageYield + 0.8,
            averageYield + 1.5, // peak
            averageYield + 0.4,
            averageYield - 0.2,
            averageYield - 1.0
        )
    }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(averageYield) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Lactation Curve",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Avg Yield: ${"%.1f".format(averageYield)} Liters/Day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .background(MintGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Peak: ${"%.1f".format(dailyData.maxOrNull() ?: averageYield)}L",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Draw the custom line chart with grid lines and a bezier path
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val primaryColor = MintGreen
            val secondaryColor = ForestGreen
            val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingX = 40.dp.toPx()
                val paddingY = 20.dp.toPx()

                val chartWidth = width - paddingX * 2
                val chartHeight = height - paddingY * 2

                val minVal = (dailyData.minOrNull() ?: 0.0) - 2.0
                val maxVal = (dailyData.maxOrNull() ?: 10.0) + 2.0
                val range = maxVal - minVal

                // Draw Horizontal Grid Lines
                val gridLinesCount = 3
                for (i in 0..gridLinesCount) {
                    val y = paddingY + chartHeight * i / gridLinesCount
                    drawLine(
                        color = gridColor,
                        start = Offset(paddingX, y),
                        end = Offset(width - paddingX, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Compute point coordinates
                val points = dailyData.mapIndexed { idx, value ->
                    val x = paddingX + chartWidth * idx / (dailyData.size - 1)
                    val yNormalized = ((value - minVal) / range).toFloat()
                    val y = paddingY + chartHeight * (1f - yNormalized * animationProgress.value)
                    Offset(x, y)
                }

                // Draw Bezier Path
                if (points.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cp1 = Offset(prev.x + (curr.x - prev.x) / 2f, prev.y)
                            val cp2 = Offset(prev.x + (curr.x - prev.x) / 2f, curr.y)
                            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, curr.x, curr.y)
                        }
                    }

                    // Draw Stroke
                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Gradient Fill under the line
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().x, height - paddingY)
                        lineTo(points.first().x, height - paddingY)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                            startY = paddingY,
                            endY = height - paddingY
                        )
                    )

                    // Draw Nodes and Highlight Peak
                    points.forEachIndexed { index, point ->
                        val isPeak = dailyData[index] == dailyData.maxOrNull()
                        drawCircle(
                            color = if (isPeak) secondaryColor else primaryColor,
                            radius = if (isPeak) 6.dp.toPx() else 4.dp.toPx(),
                            center = point
                        )
                        if (isPeak) {
                            drawCircle(
                                color = secondaryColor.copy(alpha = 0.3f),
                                radius = 10.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
