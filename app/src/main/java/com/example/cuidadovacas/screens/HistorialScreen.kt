package com.example.cuidadovacas.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme

@Composable
fun HistorialScreen(
    ventilationOpen: Boolean = false,
    onVentilationChange: (Boolean) -> Unit = {},
    lightIntensity: Float = 60f,
    onLightIntensityChange: (Float) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Últimas 24h") }
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
                listOf("Hoy", "Últimas 24h", "Semana", "Mes").forEach { filter ->
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

            // 2. GRÁFICO 24 HORAS EN CANVAS
            Graph24HoursCard(selectedFilter = selectedFilter)

            Text(
                text = "Panel de Control",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 3. PANEL DE CONTROL (ACTUADORES)
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

@Composable
fun Graph24HoursCard(selectedFilter: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tendencia de $selectedFilter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Temperatura y Humedad Sincronizadas",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🌡️ Temp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "💧 Hum",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = size.width
                val height = size.height

                // Líneas de cuadrícula horizontales
                val lines = 4
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

                // Curva de Temperatura (Valores de ejemplo)
                val tempPoints = listOf(
                    Offset(0f, height * 0.65f),
                    Offset(width * 0.2f, height * 0.8f),
                    Offset(width * 0.4f, height * 0.55f),
                    Offset(width * 0.6f, height * 0.25f),
                    Offset(width * 0.8f, height * 0.35f),
                    Offset(width, height * 0.4f)
                )

                val tempPath = Path().apply {
                    moveTo(tempPoints[0].x, tempPoints[0].y)
                    for (i in 0 until tempPoints.size - 1) {
                        val p1 = tempPoints[i]
                        val p2 = tempPoints[i + 1]
                        cubicTo(
                            (p1.x + p2.x) / 2, p1.y,
                            (p1.x + p2.x) / 2, p2.y,
                            p2.x, p2.y
                        )
                    }
                }

                val tempFillPath = Path().apply {
                    addPath(tempPath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = tempFillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
                drawPath(
                    path = tempPath,
                    color = primaryColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Curva de Humedad (Valores inversos)
                val humPoints = listOf(
                    Offset(0f, height * 0.3f),
                    Offset(width * 0.2f, height * 0.15f),
                    Offset(width * 0.4f, height * 0.4f),
                    Offset(width * 0.6f, height * 0.75f),
                    Offset(width * 0.8f, height * 0.6f),
                    Offset(width, height * 0.55f)
                )

                val humPath = Path().apply {
                    moveTo(humPoints[0].x, humPoints[0].y)
                    for (i in 0 until humPoints.size - 1) {
                        val p1 = humPoints[i]
                        val p2 = humPoints[i + 1]
                        cubicTo(
                            (p1.x + p2.x) / 2, p1.y,
                            (p1.x + p2.x) / 2, p2.y,
                            p2.x, p2.y
                        )
                    }
                }

                val humFillPath = Path().apply {
                    addPath(humPath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = humFillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(tertiaryColor.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
                drawPath(
                    path = humPath,
                    color = tertiaryColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Puntos destacados
                tempPoints.forEach { pt ->
                    drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = pt)
                }
                humPoints.forEach { pt ->
                    drawCircle(color = tertiaryColor, radius = 5.dp.toPx(), center = pt)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("00:00", "05:00", "10:00", "15:00", "20:00", "Ahora").forEach { hour ->
                    Text(
                        text = hour,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialScreenPreview() {
    CuidadoVacasTheme {
        HistorialScreen()
    }
}