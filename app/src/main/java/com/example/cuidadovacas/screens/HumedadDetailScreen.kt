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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme

@Composable
fun HumedadDetailScreen(
    currentHumidity: Int = 55,
    onNavigateBack: () -> Unit = {}
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
                // Top Bar Navigation
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
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⬅️", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Registro de Humedad",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextColorMain
                    )
                }
            }

            // Métrica Principal
            item {
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
                                text = "Humedad Relativa",
                                color = TextColorSec,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$currentHumidity %",
                                color = TextColorMain,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ColorSkyBlueDark.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💧", fontSize = 32.sp)
                        }
                    }
                }
            }

            // Diagnóstico y Riesgo Veterinario
            item {
                HumidityRiskDiagnosticCard(humidity = currentHumidity)
            }

            item {
                Text(
                    text = "Registro de las últimas 24 horas",
                    color = TextColorMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            // Timeline de Historial Sincronizado
            items(sampleHourlyReadings) { reading ->
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
                                            reading.humidity < 40 -> ColorWarmYellowDark
                                            reading.humidity > 70 -> Color(0xFFEF4444)
                                            else -> ColorMeadowGreen
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${reading.time} hrs",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextColorMain
                            )
                        }

                        Text(
                            text = "${reading.humidity} %",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColorMain
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HumidityRiskDiagnosticCard(humidity: Int) {
    val (bgColor, textColor, titleText, descText) = when {
        humidity < 40 -> HumidityDiagnosticData(
            Color(0xFFFFFBEB),
            Color(0xFFB45309),
            "⚠️ Ambiente Seco (< 40%)",
            "Humedad baja. Riesgo de polvo en suspensión e irritación de vías respiratorias."
        )
        humidity > 70 -> HumidityDiagnosticData(
            Color(0xFFFEF2F2),
            Color(0xFFB91C1C),
            "🚨 Riesgo Alto (> 70%)",
            "Humedad excesiva. Dificulta la disipación de calor por jadeo y favorece proliferación de bacterias ambientales (mastitis)."
        )
        else -> HumidityDiagnosticData(
            Color(0xFFECFDF5),
            Color(0xFF047857),
            "✅ Zona de Confort (40% - 70%)",
            "Condiciones óptimas para la termorregulación y salud respiratoria."
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

private data class HumidityDiagnosticData(
    val bgColor: Color,
    val textColor: Color,
    val titleText: String,
    val descText: String
)

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun HumedadDetailScreenPreview() {
    CuidadoVacasTheme {
        HumedadDetailScreen(
            currentHumidity = 55,
            onNavigateBack = {}
        )
    }
}