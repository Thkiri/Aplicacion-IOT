package com.example.cuidadovacas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==========================================
// PALETA DE COLORES (MODO CLARO Y OSCURO)
// ==========================================
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1E293B)
val LightOnSurfaceVariant = Color(0xFF64748B)
val LightPrimary = Color(0xFF0284C7)      // Celeste Acento
val LightSecondary = Color(0xFF10B981)    // Verde Prado
val LightTertiary = Color(0xFFF59E0B)     // Naranja/Amarillo Cálido

val DarkBackground = Color(0xFF0F172A)     // Azul/Gris Muy Oscuro
val DarkSurface = Color(0xFF1E293B)        // Gris Oscuro para Tarjetas
val DarkOnSurface = Color(0xFFF8FAFC)      // Texto Blanco/Gris Claro
val DarkOnSurfaceVariant = Color(0xFF94A3B8)
val DarkPrimary = Color(0xFF38BDF8)       // Celeste Acento Claro
val DarkSecondary = Color(0xFF4ADE80)     // Verde Prado Claro
val DarkTertiary = Color(0xFFFBBF24)      // Amarillo Cálido Claro

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = LightOnSurfaceVariant
)

@Composable
fun CuidadoVacasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}