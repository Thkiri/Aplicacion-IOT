package com.example.cuidadovacas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme

// ==========================================
// PALETA DE COLORES (Inspiración Campo/Naturaleza)
// ==========================================
val BgColor = Color(0xFFF8FAFC)           // Blanco humo / Gris ultra claro
val CardColor = Color(0xFFFFFFFF)         // Blanco puro

val ColorSkyBlue = Color(0xFF38BDF8)      // Celeste Cielo
val ColorSkyBlueDark = Color(0xFF0284C7)  // Azul Acento (Botones/Acciones)
val ColorMeadowGreen = Color(0xFF4ADE80)  // Verde Prado Suave
val ColorMeadowGreenDark = Color(0xFF10B981) // Verde Fuerte para textos
val ColorWarmYellow = Color(0xFFFBBF24)   // Amarillo Cálido
val ColorWarmYellowDark = Color(0xFFF59E0B)// Naranja/Amarillo oscuro

// ==========================================
// MODELO Y DATOS DE HISTORIAL COMPARTIDOS
// ==========================================
data class HourlyReading(
    val time: String,
    val temperature: Float,
    val humidity: Int
)

// Lista sincronizada de 24 horas
val sampleHourlyReadings = listOf(
    HourlyReading("14:00", 24.5f, 55),
    HourlyReading("13:00", 25.1f, 45),
    HourlyReading("12:00", 24.8f, 42),
    HourlyReading("11:00", 23.8f, 50),
    HourlyReading("10:00", 22.5f, 58),
    HourlyReading("09:00", 21.0f, 62),
    HourlyReading("08:00", 19.5f, 68),
    HourlyReading("07:00", 18.2f, 74),
    HourlyReading("06:00", 16.8f, 78),
    HourlyReading("05:00", 15.5f, 82),
    HourlyReading("04:00", 14.2f, 85),
    HourlyReading("03:00", 14.8f, 84),
    HourlyReading("02:00", 15.0f, 80),
    HourlyReading("01:00", 15.8f, 76),
    HourlyReading("00:00", 16.5f, 72),
    HourlyReading("23:00", 17.2f, 68),
    HourlyReading("22:00", 18.0f, 65),
    HourlyReading("21:00", 19.2f, 61),
    HourlyReading("20:00", 20.1f, 58),
    HourlyReading("19:00", 21.3f, 56),
    HourlyReading("18:00", 22.0f, 54),
    HourlyReading("17:00", 23.1f, 51),
    HourlyReading("16:00", 24.0f, 49),
    HourlyReading("15:00", 24.8f, 47)
)

// ==========================================
// CONTENEDOR DE NAVEGACIÓN INTERNA DASHBOARD
// ==========================================
@Composable
fun DashboardEstabloScreen() {
    var currentScreen by remember { mutableStateOf("dashboard") }
    
    var temperature by remember { mutableFloatStateOf(24.5f) }
    var humidity by remember { mutableIntStateOf(55) }
    var lightLevel by remember { mutableIntStateOf(75) }
    var thiIndex by remember { mutableIntStateOf(68) }

    when (currentScreen) {
        "dashboard" -> MainDashboardCombinedContent(
            temperature = temperature,
            humidity = humidity,
            lightLevel = lightLevel,
            thiIndex = thiIndex,
            onNavigateToTempDetail = { currentScreen = "temp_detail" },
            onNavigateToHumedadDetail = { currentScreen = "humedad_detail" }
        )
        "temp_detail" -> TemperaturaDetailScreen(
            currentTemperature = temperature,
            onBack = { currentScreen = "dashboard" }
        )
        "humedad_detail" -> HumedadDetailScreen(
            currentHumidity = humidity,
            onNavigateBack = { currentScreen = "dashboard" }
        )
    }
}

// ==========================================
// VISTA PRINCIPAL DASHBOARD DE MONITOREO
// ==========================================
@Composable
fun MainDashboardCombinedContent(
    temperature: Float = 24.5f,
    humidity: Int = 55,
    lightLevel: Int = 75,
    thiIndex: Int = 68,
    onNavigateToTempDetail: () -> Unit = {},
    onNavigateToHumedadDetail: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val last12Readings = remember { sampleHourlyReadings.take(12) }

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
            TopBar()
            
            // 1. RESUMEN SUPERIOR COMPACTO (4 MINI TARJETAS)
            Text(
                text = "Resumen de Sensores",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 0.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniSensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Temp",
                    value = "$temperature°C",
                    iconEmoji = "🌡️",
                    accentColor = ColorSkyBlue,
                    onClick = onNavigateToTempDetail
                )
                MiniSensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Humedad",
                    value = "$humidity%",
                    iconEmoji = "💧",
                    accentColor = ColorSkyBlue,
                    onClick = onNavigateToHumedadDetail
                )
                MiniSensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Luz",
                    value = "$lightLevel%",
                    iconEmoji = "☀️",
                    accentColor = ColorWarmYellow
                )
                MiniSensorCard(
                    modifier = Modifier.weight(1f),
                    title = "ITH",
                    value = "$thiIndex",
                    iconEmoji = "🌿",
                    accentColor = ColorMeadowGreen
                )
            }

            // 2. TABLAS DE MONITOREO (ÚLTIMAS 12 HORAS)
            TemperatureTable12HoursCard(readings = last12Readings)

            HumidityTable12HoursCard(readings = last12Readings)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// TABLA 1: REGISTRO DE TEMPERATURA (12 HRS)
// ==========================================
@Composable
fun TemperatureTable12HoursCard(readings: List<HourlyReading>) {
    var selectedReading by remember { mutableStateOf<HourlyReading?>(readings.firstOrNull()) }

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
                        text = "Registro de Temperatura",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Últimas 12 Horas • Toque para detalles",
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

            // Tooltip flotante interactivo
            selectedReading?.let { reading ->
                val (statusText, statusColor) = when {
                    reading.temperature > 24.0f -> "Riesgo de Estrés Calórico" to Color(0xFFEF4444)
                    reading.temperature > 22.0f -> "Temperatura Elevada" to Color(0xFFF59E0B)
                    else -> "Zona de Confort" to Color(0xFF10B981)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${reading.time} hrs • $statusText",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${reading.temperature} °C",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Filas de la tabla (12 horas)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                readings.forEach { reading ->
                    val isSelected = (selectedReading?.time == reading.time)
                    val statusDotColor = when {
                        reading.temperature > 24.0f -> Color(0xFFEF4444)
                        reading.temperature > 22.0f -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable { selectedReading = reading }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(statusDotColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${reading.time} hrs",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${reading.temperature} °C",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TABLA 2: REGISTRO DE HUMEDAD (12 HRS)
// ==========================================
@Composable
fun HumidityTable12HoursCard(readings: List<HourlyReading>) {
    var selectedReading by remember { mutableStateOf<HourlyReading?>(readings.firstOrNull()) }
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
                        text = "Registro de Humedad",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Últimas 12 Horas • Toque para detalles",
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

            selectedReading?.let { reading ->
                val (statusText, statusColor) = when {
                    reading.humidity < 40 -> "Ambiente Seco (<40%)" to Color(0xFFF59E0B)
                    reading.humidity > 70 -> "Humedad Excesiva (>70%)" to Color(0xFFEF4444)
                    else -> "Zona de Confort (40-70%)" to Color(0xFF10B981)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${reading.time} hrs • $statusText",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${reading.humidity} %",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                readings.forEach { reading ->
                    val isSelected = (selectedReading?.time == reading.time)
                    val statusDotColor = when {
                        reading.humidity < 40 -> Color(0xFFF59E0B)
                        reading.humidity > 70 -> Color(0xFFEF4444)
                        else -> Color(0xFF10B981)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) humidityBlue.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable { selectedReading = reading }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(statusDotColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${reading.time} hrs",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${reading.humidity} %",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniSensorCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    iconEmoji: String,
    accentColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TemperaturaDetailScreen(
    currentTemperature: Float = 24.5f,
    onBack: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                DetailTopBar(title = "Registro de Temperatura", onBack = onBack)
            }

            // MÉTRICA PRINCIPAL
            item {
                MainMetricCard(
                    value = "$currentTemperature °C",
                    label = "Temperatura Actual",
                    iconEmoji = "🌡️"
                )
            }

            // DIAGNÓSTICO DE RIESGOS BIOCLIMÁTICOS (BOVINOS)
            item {
                RiskDiagnosticCard(temperature = currentTemperature)
            }

            item {
                Text(
                    text = "Últimas 24 Horas",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            // HISTORIAL TIMELINE
            items(sampleHourlyReadings) { reading ->
                TimelineItemRow(time = reading.time, temperature = reading.temperature)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// COMPONENTES REUTILIZABLES
// ==========================================

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Establo Smart",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ColorMeadowGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "En línea - Última lectura: Ahora",
                    fontSize = 13.sp,
                    color = ColorMeadowGreenDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🐮", fontSize = 32.sp)
        }
    }
}

@Composable
fun DetailTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⬅️", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MainMetricCard(value: String, label: String, iconEmoji: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(ColorSkyBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun RiskDiagnosticCard(temperature: Float) {
    val (bgAlphaColor, textColor, titleText, descText) = when {
        temperature > 24.0f -> Quadruple(
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            "⚠️ Riesgo de Estrés Calórico",
            "Disminución en consumo de alimento y producción de leche. Requiere ventilación."
        )
        temperature < 5.0f -> Quadruple(
            Color(0xFF3B82F6),
            Color(0xFF2563EB),
            "❄️ Riesgo de Estrés por Frío",
            "Aumento de requerimiento energético. Cuidar terneros."
        )
        else -> Quadruple(
            Color(0xFF10B981),
            Color(0xFF059669),
            "✅ Zona Térmica de Confort",
            "Condiciones óptimas para el ganado bovino."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgAlphaColor.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = titleText,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = descText,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
fun TimelineItemRow(time: String, temperature: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                temperature > 24.0f -> ColorWarmYellowDark
                                temperature < 5.0f -> ColorSkyBlueDark
                                else -> ColorMeadowGreen
                            }
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$time hrs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$temperature °C",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardEstabloScreenPreviewLight() {
    CuidadoVacasTheme(darkTheme = false) {
        DashboardEstabloScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardEstabloScreenPreviewDark() {
    CuidadoVacasTheme(darkTheme = true) {
        DashboardEstabloScreen()
    }
}