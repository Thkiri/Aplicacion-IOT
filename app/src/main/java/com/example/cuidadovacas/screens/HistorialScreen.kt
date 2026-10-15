package com.example.cuidadovacas.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme
import kotlin.math.roundToInt

// ==========================================
// ESTRUCTURA DE DATOS PARA PUNTOS DE GRÁFICA
// ==========================================
data class ChartPoint(
    val label: String,
    val value: Float,
    val unit: String
)

@Composable
fun HistorialScreen(
    ventilationOpen: Boolean = false,
    onVentilationChange: (Boolean) -> Unit = {},
    lightIntensity: Float = 60f,
    onLightIntensityChange: (Float) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Últimas 12h") }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Historial Bioclimático",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 1. FILTROS RÁPIDOS (CHIPS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Hoy", "Últimas 12h", "Últimas 24h", "Semana").forEach { filter ->
                    val isSelected = (selectedFilter == filter)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { /* Abrir DatePicker */ }) {
                    Text(text = "📅", fontSize = 20.sp)
                }
            }

            // 2. GRÁFICA SUPERIOR INTERACTIVA: TEMPERATURA (12 PUNTOS)
            TemperatureChartCard12H(filterName = selectedFilter)

            // 3. GRÁFICA INFERIOR INTERACTIVA: HUMEDAD (12 PUNTOS)
            HumidityChartCard12H(filterName = selectedFilter)

            Text(
                text = "Panel de Control",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 4. PANEL DE CONTROL (ACTUADORES)
            ActuatorSwitchCard(
                title = "Ventilación / Ventanas",
                isActive = ventilationOpen,
                onCheckedChange = onVentilationChange,
                iconEmoji = "🌬️",
                activeColor = MaterialTheme.colorScheme.primary
            )

            ActuatorSliderCard(
                title = "Iluminación del Galpón",
                value = lightIntensity,
                onValueChange = onLightIntensityChange,
                iconEmoji = "💡",
                accentColor = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// GRÁFICA 1: TEMPERATURA 12 PUNTOS CON CROSSHAIR Y TOOLTIP
// ==========================================
@Composable
fun TemperatureChartCard12H(filterName: String) {
    // 12 Nodos/Puntos de datos seleccionables (últimas 12 horas)
    val points = remember {
        listOf(
            ChartPoint("03:00 hrs", 14.8f, "°C"),
            ChartPoint("04:00 hrs", 14.2f, "°C"),
            ChartPoint("05:00 hrs", 15.5f, "°C"),
            ChartPoint("06:00 hrs", 16.8f, "°C"),
            ChartPoint("07:00 hrs", 18.2f, "°C"),
            ChartPoint("08:00 hrs", 19.5f, "°C"),
            ChartPoint("09:00 hrs", 21.0f, "°C"),
            ChartPoint("10:00 hrs", 22.5f, "°C"),
            ChartPoint("11:00 hrs", 23.8f, "°C"),
            ChartPoint("12:00 hrs", 24.8f, "°C"),
            ChartPoint("13:00 hrs", 25.1f, "°C"),
            ChartPoint("14:00 hrs", 24.5f, "°C")
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(11) } // Seleccionado por defecto: la última hora

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tendencia de Temperatura",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Toque un punto para consultar ($filterName)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "🌡️ °C",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TOOLTIP FLOTANTE ANALÍTICO AL SELECCIONAR PUNTO
            val activePoint = selectedIndex?.let { points.getOrNull(it) }
            if (activePoint != null) {
                val activeColor = getTempColor(activePoint.value)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(activeColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = activePoint.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${activePoint.value} °C",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = activeColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

            // CANVAS CON TAP INTERACTIVO Y CROSSHAIR
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val stepX = width / (points.size - 1)
                            val index = (offset.x / stepX).roundToInt().coerceIn(0, points.size - 1)
                            selectedIndex = index
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                // Cuadrícula horizontal
                val lines = 3
                for (i in 0..lines) {
                    val y = height * (i.toFloat() / lines)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // Cálculo de coordenadas (10°C a 30°C)
                val minVal = 10f
                val maxVal = 30f
                val calculatedPoints = points.mapIndexed { index, pt ->
                    val x = index * (width / (points.size - 1))
                    val normalized = (pt.value - minVal) / (maxVal - minVal)
                    val y = height * (1f - normalized.coerceIn(0f, 1f))
                    Offset(x, y)
                }

                val path = Path().apply {
                    moveTo(calculatedPoints[0].x, calculatedPoints[0].y)
                    for (i in 0 until calculatedPoints.size - 1) {
                        val p1 = calculatedPoints[i]
                        val p2 = calculatedPoints[i + 1]
                        cubicTo((p1.x + p2.x) / 2, p1.y, (p1.x + p2.x) / 2, p2.y, p2.x, p2.y)
                    }
                }

                // Fondo degradado
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF97316).copy(alpha = 0.25f), Color.Transparent)
                    )
                )

                // Trazo principal
                drawPath(
                    path = path,
                    color = Color(0xFFF97316),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // LÍNEA GUÍA VERTICAL (CROSSHAIR) AL EJE X
                selectedIndex?.let { idx ->
                    if (idx in calculatedPoints.indices) {
                        val selPt = calculatedPoints[idx]
                        drawLine(
                            color = gridColor,
                            start = Offset(selPt.x, 0f),
                            end = Offset(selPt.x, height),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }
                }

                // DIBUJAR PUNTOS Y NODOS CON RESALTADO
                calculatedPoints.forEachIndexed { idx, pt ->
                    val ptVal = points[idx].value
                    val ptColor = getTempColor(ptVal)
                    val isSel = (idx == selectedIndex)

                    if (isSel) {
                        // Halo ilumando y agrandado
                        drawCircle(color = ptColor.copy(alpha = 0.35f), radius = 12.dp.toPx(), center = pt)
                        drawCircle(color = ptColor, radius = 7.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = pt)
                    } else {
                        drawCircle(color = ptColor, radius = 4.5.dp.toPx(), center = pt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ETIQUETAS DE HORAS EJE X
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEachIndexed { idx, pt ->
                    val isKeyLabel = (idx % 2 == 0 || idx == points.size - 1)
                    val isSel = (idx == selectedIndex)

                    Text(
                        text = if (isKeyLabel) pt.label.replace(" hrs", "") else "•",
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==========================================
// GRÁFICA 2: HUMEDAD 12 PUNTOS CON CROSSHAIR Y TOOLTIP
// ==========================================
@Composable
fun HumidityChartCard12H(filterName: String) {
    // 12 Nodos/Puntos de datos seleccionables (últimas 12 horas)
    val points = remember {
        listOf(
            ChartPoint("03:00 hrs", 84f, "%"),
            ChartPoint("04:00 hrs", 85f, "%"),
            ChartPoint("05:00 hrs", 82f, "%"),
            ChartPoint("06:00 hrs", 78f, "%"),
            ChartPoint("07:00 hrs", 74f, "%"),
            ChartPoint("08:00 hrs", 68f, "%"),
            ChartPoint("09:00 hrs", 62f, "%"),
            ChartPoint("10:00 hrs", 58f, "%"),
            ChartPoint("11:00 hrs", 50f, "%"),
            ChartPoint("12:00 hrs", 42f, "%"),
            ChartPoint("13:00 hrs", 45f, "%"),
            ChartPoint("14:00 hrs", 55f, "%")
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(11) } // Seleccionado por defecto: la última hora
    val humidityBlue = Color(0xFF0284C7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tendencia de Humedad",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Toque un punto para consultar ($filterName)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "💧 %",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = humidityBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TOOLTIP FLOTANTE ANALÍTICO AL SELECCIONAR PUNTO
            val activePoint = selectedIndex?.let { points.getOrNull(it) }
            if (activePoint != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(humidityBlue)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = activePoint.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${activePoint.value.toInt()} %",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = humidityBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

            // CANVAS CON TAP INTERACTIVO Y CROSSHAIR
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val stepX = width / (points.size - 1)
                            val index = (offset.x / stepX).roundToInt().coerceIn(0, points.size - 1)
                            selectedIndex = index
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                // Cuadrícula
                val lines = 3
                for (i in 0..lines) {
                    val y = height * (i.toFloat() / lines)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // Cálculo de coordenadas (30% a 100%)
                val minVal = 30f
                val maxVal = 100f
                val calculatedPoints = points.mapIndexed { index, pt ->
                    val x = index * (width / (points.size - 1))
                    val normalized = (pt.value - minVal) / (maxVal - minVal)
                    val y = height * (1f - normalized.coerceIn(0f, 1f))
                    Offset(x, y)
                }

                val path = Path().apply {
                    moveTo(calculatedPoints[0].x, calculatedPoints[0].y)
                    for (i in 0 until calculatedPoints.size - 1) {
                        val p1 = calculatedPoints[i]
                        val p2 = calculatedPoints[i + 1]
                        cubicTo((p1.x + p2.x) / 2, p1.y, (p1.x + p2.x) / 2, p2.y, p2.x, p2.y)
                    }
                }

                // Fondo degradado azul
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(humidityBlue.copy(alpha = 0.25f), Color.Transparent)
                    )
                )

                // Trazo principal
                drawPath(
                    path = path,
                    color = humidityBlue,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // LÍNEA GUÍA VERTICAL (CROSSHAIR) AL EJE X
                selectedIndex?.let { idx ->
                    if (idx in calculatedPoints.indices) {
                        val selPt = calculatedPoints[idx]
                        drawLine(
                            color = gridColor,
                            start = Offset(selPt.x, 0f),
                            end = Offset(selPt.x, height),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }
                }

                // DIBUJAR PUNTOS Y NODOS CON RESALTADO
                calculatedPoints.forEachIndexed { idx, pt ->
                    val isSel = (idx == selectedIndex)
                    if (isSel) {
                        drawCircle(color = humidityBlue.copy(alpha = 0.35f), radius = 12.dp.toPx(), center = pt)
                        drawCircle(color = humidityBlue, radius = 7.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = pt)
                    } else {
                        drawCircle(color = humidityBlue, radius = 4.5.dp.toPx(), center = pt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ETIQUETAS DE HORAS EJE X
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEachIndexed { idx, pt ->
                    val isKeyLabel = (idx % 2 == 0 || idx == points.size - 1)
                    val isSel = (idx == selectedIndex)

                    Text(
                        text = if (isKeyLabel) pt.label.replace(" hrs", "") else "•",
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isSel) humidityBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getTempColor(value: Float): Color {
    return when {
        value > 24.0f -> Color(0xFFEF4444) // Rojo (Estrés calórico)
        value > 22.0f -> Color(0xFFF59E0B) // Ámbar / Naranja
        else -> Color(0xFF10B981)           // Verde (Confort / Óptimo)
    }
}

@Preview(showBackground = true, name = "Historial Modo Claro")
@Composable
fun HistorialScreenPreviewLight() {
    CuidadoVacasTheme(darkTheme = false) {
        HistorialScreen()
    }
}

@Preview(showBackground = true, name = "Historial Modo Oscuro")
@Composable
fun HistorialScreenPreviewDark() {
    CuidadoVacasTheme(darkTheme = true) {
        HistorialScreen()
    }
}