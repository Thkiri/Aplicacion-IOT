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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.cuidadovacas.components.SensorCard
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme
import kotlin.math.roundToInt

// ==========================================
// PALETA DE COLORES (Inspiración Campo/Naturaleza)
// ==========================================
val BgColor = Color(0xFFF8FAFC)           // Blanco humo / Gris ultra claro
val CardColor = Color(0xFFFFFFFF)         // Blanco puro
val TextColorMain = Color(0xFF334155)     // Gris pizarra oscuro (Slate)
val TextColorSec = Color(0xFF64748B)      // Gris medio

val ColorSkyBlue = Color(0xFF38BDF8)      // Celeste Cielo
val ColorSkyBlueDark = Color(0xFF0284C7)  // Azul Acento (Botones/Acciones)
val ColorMeadowGreen = Color(0xFF4ADE80)  // Verde Prado Suave
val ColorMeadowGreenDark = Color(0xFF10B981) // Verde Fuerte para textos
val ColorWarmYellow = Color(0xFFFBBF24)   // Amarillo Cálido
val ColorWarmYellowDark = Color(0xFFF59E0B)// Naranja/Amarillo oscuro

val AlertBgColor = Color(0xFFF1F5F9)      // Fondo para la tarjeta de alerta

// ==========================================
// MODELO Y DATOS DE HISTORIAL COMPARTIDOS
// ==========================================
data class HourlyReading(
    val time: String,
    val temperature: Float,
    val humidity: Int
)

// Lista sincronizada de 24 horas (Relación física real: mayor temperatura = menor humedad relativa)
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
// PANTALLA PRINCIPAL CON NAVEGACIÓN Y ESTADO
// ==========================================
@Composable
fun DashboardEstabloScreen() {
    var currentScreen by remember { mutableStateOf("dashboard") }
    
    // Estado compartido de variables IoT (Sincronizado con la lectura de las 14:00)
    var temperature by remember { mutableFloatStateOf(24.5f) }
    var humidity by remember { mutableIntStateOf(55) }
    var lightLevel by remember { mutableIntStateOf(75) }
    var thiIndex by remember { mutableIntStateOf(68) }
    
    var ventilationOpen by remember { mutableStateOf(false) }
    var lightIntensity by remember { mutableFloatStateOf(60f) }

    when (currentScreen) {
        "dashboard" -> MainDashboardContent(
            temperature = temperature,
            humidity = humidity,
            lightLevel = lightLevel,
            thiIndex = thiIndex,
            ventilationOpen = ventilationOpen,
            onVentilationChange = { ventilationOpen = it },
            lightIntensity = lightIntensity,
            onLightIntensityChange = { lightIntensity = it },
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

@Composable
fun MainDashboardContent(
    temperature: Float,
    humidity: Int,
    lightLevel: Int,
    thiIndex: Int,
    ventilationOpen: Boolean,
    onVentilationChange: (Boolean) -> Unit,
    lightIntensity: Float,
    onLightIntensityChange: (Float) -> Unit,
    onNavigateToTempDetail: () -> Unit,
    onNavigateToHumedadDetail: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgColor
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
            
            Text(
                text = "Panel de Sensores",
                color = TextColorMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 0.dp)
            )

            // GRID DE 4 SENSORES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Temperatura",
                    value = "$temperature °C",
                    status = if (temperature > 24.0f) "Estrés Calórico" else "Estado: Confort",
                    statusColor = if (temperature > 24.0f) ColorWarmYellowDark else ColorMeadowGreenDark,
                    iconEmoji = "🌡️",
                    iconTint = ColorSkyBlue,
                    onClick = onNavigateToTempDetail
                )
                SensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Humedad",
                    value = "$humidity %",
                    status = if (humidity in 40..70) "Óptima" else if (humidity < 40) "Seca" else "Excesiva",
                    statusColor = if (humidity in 40..70) ColorMeadowGreenDark else ColorWarmYellowDark,
                    iconEmoji = "💧",
                    iconTint = ColorSkyBlue,
                    onClick = onNavigateToHumedadDetail
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Fotoperiodo",
                    value = "$lightLevel %",
                    status = "Luz natural",
                    statusColor = TextColorSec,
                    iconEmoji = "☀️",
                    iconTint = ColorWarmYellow
                )
                SensorCard(
                    modifier = Modifier.weight(1f),
                    title = "Índice ITH",
                    value = "$thiIndex",
                    status = "Sin estrés térmico",
                    statusColor = ColorMeadowGreenDark,
                    iconEmoji = "🌿",
                    iconTint = ColorMeadowGreen
                )
            }

            Text(
                text = "Panel de Control",
                color = TextColorMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
            )

            // ACTUADORES
            ActuatorSwitchCard(
                title = "Ventilación / Ventanas",
                isActive = ventilationOpen,
                onCheckedChange = onVentilationChange,
                iconEmoji = "🌬️",
                activeColor = ColorSkyBlue
            )

            ActuatorSliderCard(
                title = "Iluminación del Galpón",
                value = lightIntensity,
                onValueChange = onLightIntensityChange,
                iconEmoji = "💡",
                accentColor = ColorWarmYellow
            )

            // ALERTAS RECIENTES
            Spacer(modifier = Modifier.height(8.dp))
            AlertCard(message = "⚠️ 14:30 - Ventilación activada automáticamente (Temp > 24°C)")
            Spacer(modifier = Modifier.height(24.dp))
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
        containerColor = BgColor
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
                    color = TextColorMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            // HISTORIAL TIMELINE (Usando los datos de prueba compartidos)
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
                color = TextColorMain
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
                .background(CardColor),
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
                .background(CardColor)
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
            color = TextColorMain
        )
    }
}

@Composable
fun MainMetricCard(value: String, label: String, iconEmoji: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
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
                    color = TextColorSec,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    color = TextColorMain,
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
    val (bgColor, textColor, titleText, descText) = when {
        temperature > 24.0f -> Quadruple(
            Color(0xFFFFF7ED),
            Color(0xFFC2410C),
            "⚠️ Riesgo de Estrés Calórico",
            "Disminución en consumo de alimento y producción de leche. Requiere ventilación."
        )
        temperature < 5.0f -> Quadruple(
            Color(0xFFEFF6FF),
            Color(0xFF1D4ED8),
            "❄️ Riesgo de Estrés por Frío",
            "Aumento de requerimiento energético. Cuidar terneros."
        )
        else -> Quadruple(
            Color(0xFFECFDF5),
            Color(0xFF047857),
            "✅ Zona Térmica de Confort",
            "Condiciones óptimas para el ganado bovino."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
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
                color = TextColorMain,
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
        colors = CardDefaults.cardColors(containerColor = CardColor),
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
                    color = TextColorMain
                )
            }

            Text(
                text = "$temperature °C",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorMain
            )
        }
    }
}

@Composable
fun ActuatorSwitchCard(
    title: String,
    isActive: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconEmoji: String,
    activeColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isActive) activeColor.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 24.sp)
                }
                Column {
                    Text(
                        text = title,
                        color = TextColorMain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isActive) "ABIERTAS" else "CERRADAS",
                        color = if (isActive) ColorSkyBlueDark else TextColorSec,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            
            Switch(
                checked = isActive,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = activeColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE2E8F0),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun ActuatorSliderCard(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    iconEmoji: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = title,
                            color = TextColorMain,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Intensidad: ${value.roundToInt()}%",
                            color = ColorWarmYellowDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun AlertCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AlertBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = TextColorMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun DashboardEstabloScreenPreview() {
    CuidadoVacasTheme {
        DashboardEstabloScreen()
    }
}