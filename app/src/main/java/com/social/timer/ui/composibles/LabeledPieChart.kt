package com.social.timer.ui.composibles

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.social.timer.ui.theme.outfitFontFamily
import kotlin.math.cos
import kotlin.math.sin

// 1. Define data structure for the chart slices
data class PieSlice(
    val value: Float,
    val label: String,
    val color: Color?,
    val timing: String?
)

@Composable
fun LabeledPieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier
) {
    val totalValue = slices.sumOf { it.value.toDouble() }.toFloat()
    val context = LocalContext.current

    // Fallback if data is empty or zero
    if (totalValue == 0f) return
    val resolver = LocalFontFamilyResolver.current
    val outTypeface = remember(resolver, outfitFontFamily) {
        resolver.resolve(
            fontFamily = outfitFontFamily,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal
        ).value as android.graphics.Typeface
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val minDimension = minOf(canvasSize.width, canvasSize.height)
            val radius = minDimension / 2f
            val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

            // Adjust chart size slightly to avoid edge clipping
            val chartPadding = 20.dp.toPx()
            val chartRadius = radius - chartPadding

            val topLeft = Offset(
                center.x - chartRadius,
                center.y - chartRadius
            )
            val arcSize = Size(chartRadius * 2, chartRadius * 2)

            // Start drawing from the top (12 o'clock position = -90 degrees)
            var currentStartAngle = -90f

            slices.forEach { slice ->
                val sweepAngle = (slice.value / totalValue) * 360f

                // Draw the actual pie slice
                drawArc(
                    color = slice.color!!,
                    startAngle = currentStartAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = topLeft,
                    size = arcSize
                )

                // Calculate the middle angle of the slice for the text placement
                val middleAngleInRadians =
                    Math.toRadians((currentStartAngle + sweepAngle / 2).toDouble())

                // Position the label roughly at 60% of the radius length
                val labelRadius = chartRadius * 0.6f
                val labelX = (center.x + labelRadius * cos(middleAngleInRadians)).toFloat()
                val labelY = (center.y + labelRadius * sin(middleAngleInRadians)).toFloat()


                // Draw the text labels using the native Android Canvas
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        typeface = outTypeface
                        color = Color.White.toArgb() // Make sure label stands out
                        textSize = 10.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                        isFakeBoldText = true
                    }

                    // Adjust text bounds vertically to truly center it on the calculated dot
                    val textHeightOffset = (paint.descent() + paint.ascent()) / 2

                    canvas.nativeCanvas.drawText(
                        slice.label,
                        labelX,
                        labelY - textHeightOffset,
                        paint
                    )
                }

                currentStartAngle += sweepAngle
            }
        }
    }
}
