package com.example.moneypath.ui.elements

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moneypath.R
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.PieSlice
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


@Composable
fun CategoryDonutChart(
    slices: List<PieSlice> ,
    modifier: Modifier = Modifier,
    holeRatio: Float = 0.5f,
    index: Int
) {
    if(slices.isEmpty()){
        Box(modifier = modifier) {
            Image(
                painter = painterResource(R.drawable.empty_chart),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = if(index == 0) "Додавайте транзакції, щоб побачити статистику" else "План витрат ще не створений",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 40.dp)
            )
        }
    }else {
        val total = slices.sumOf { it.value }
        val progress = animateFloatAsState(1f, tween(1000))
        Box(modifier = modifier, contentAlignment = Alignment.Center) {

            // === CANVAS — малюємо діаграму ===
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val diameter = size.minDimension
                val radius = diameter / 2
                val holeRadius = radius * holeRatio

                var startAngle: Float = -90f

                slices.forEach { slice ->
                    val sweep = (slice.value / total * 360f) * progress.value

                    // Сектор
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep.toFloat(),
                        useCenter = true,
                        size = Size(diameter, diameter),
                        topLeft = Offset(center.x - radius, center.y - radius)
                    )

                    // Лінія від сектору
                    val midAngle = Math.toRadians((startAngle + sweep / 2).toDouble())
                    val startOffset = Offset(
                        x = center.x + cos(midAngle).toFloat() * (radius * 0.85f),
                        y = center.y + sin(midAngle).toFloat() * (radius * 0.85f)
                    )
                    val endOffset = Offset(
                        x = center.x + cos(midAngle).toFloat() * (radius * 1.7f),
                        y = center.y + sin(midAngle).toFloat() * (radius * 1.7f)
                    )

                    drawLine(
                        color = slice.color,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = 4f
                    )

                    startAngle += sweep.toFloat()
                }

                // Дірка в центрі
                drawCircle(
                    color = if (index == 0) Color(0xFF55D6BE) else Color(0xFF4FC3F7),
                    radius = holeRadius,
                    center = center
                )
            }

            // === ІКОНКИ + ПРОЦЕНТИ — другий шар ===
            PercentAndIconsOverlay(slices = slices)
        }
    }
}

@Composable
private fun PercentAndIconsOverlay(slices: List<PieSlice>) {
    val total = slices.sumOf { it.value }

    Layout(
        content = {
            slices.forEach { slice ->
                val percent = (slice.value / total * 100).toInt()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = slice.icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "$percent%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }
            }
        }
    ) { measurables, constraints ->

        val placeables = measurables.map { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {

            val cx = constraints.maxWidth / 2
            val cy = constraints.maxHeight / 2
            val radius = min(cx, cy) * 1.7f

            var startAngle: Float = -90f

            slices.forEachIndexed { index, slice ->
                val sweep: Float = (slice.value / total * 360f).toFloat()
                val angle = Math.toRadians((startAngle + sweep / 2).toDouble())

                val x = (cx + cos(angle) * radius).toInt()
                val y = (cy + sin(angle) * radius).toInt()

                placeables[index].place(
                    x - placeables[index].width / 2,
                    y - placeables[index].height / 2
                )

                startAngle += sweep
            }
        }
    }
}

@Composable
fun CategoryHorizontalBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val entries = mutableListOf<BarEntry>()
    val labels = mutableListOf<String>()
    val colors = mutableListOf<Int>()

    var visible by remember { mutableStateOf(true) }

    data.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
        entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
        labels.add(entry.key)
        val color = Categories.expensesCategory.find { it.name == entry.key }?.color?.toArgb()
        if (color != null) colors.add(color) else colors.add(Color(0xFFFDD835).toArgb())
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height((labels.size * 25).dp)
            .alpha(if (visible) 1f else 0f)
        ,
        factory = { ctx ->
            HorizontalBarChart(ctx).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setPinchZoom(false)
                legend.isEnabled = false
                setDrawValueAboveBar(true)
                setTouchEnabled(true)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawLabels(false)
                }

                axisLeft.isEnabled = false
                axisRight.apply {
                    axisMinimum = 0f
                    textSize = 10f
                }

                axisRight.setDrawGridLines(false)

                isHighlightPerTapEnabled = true
            }
        },
        update = { chart ->
            if (chart.data == null) {
                chart.data = BarData()
            }
            chart.post {
                val set = BarDataSet(entries, "").apply {
                    this.colors = colors
                    valueTextSize = 12f
                    valueFormatter = object : ValueFormatter() {
                        @SuppressLint("DefaultLocale")
                        override fun getFormattedValue(value: Float): String {
                            return if (value % 1.0f == 0f)
                                value.toInt().toString()
                            else
                                String.format("%.1f", value)
                        }
                    }
                }
                chart.data = BarData(set).apply {
                    barWidth = 0.9f
                }
                chart.setFitBars(true)
                chart.setNoDataText("")
                chart.marker = CategoryMarkerView(chart.context, labels)
                chart.invalidate()
            }
        }
    )
}

class CategoryMarkerView(
    context: Context,
    private val labels: List<String>
) : MarkerView(context, R.layout.marker_layout) {

    private val textView: TextView = findViewById(R.id.markerText)

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null && e.x.toInt() in labels.indices) {
            val categoryName = labels[e.x.toInt()]
            val value = e.y
            textView.text =
                "$categoryName: ${if (value % 1f == 0f) value.toInt() else String.format("%.1f", value)}"
        } else {
            textView.text = ""
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat() - 20f)
    }
}