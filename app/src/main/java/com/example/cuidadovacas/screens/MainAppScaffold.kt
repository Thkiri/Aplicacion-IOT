package com.example.cuidadovacas.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold() {
    val systemDark = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemDark) }
    var isLoggedIn by remember { mutableStateOf(false) }

    // PagerState para gestos de deslizamiento (3 páginas: 0 = Dashboard, 1 = Historial, 2 = Ajustes)
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Estado compartido para los actuadores en la app
    var ventilationOpen by remember { mutableStateOf(false) }
    var lightIntensity by remember { mutableFloatStateOf(60f) }

    CuidadoVacasTheme(darkTheme = isDarkMode) {
        if (!isLoggedIn) {
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true },
                isDarkMode = isDarkMode,
                onToggleDarkMode = { isDarkMode = it }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Establo Smart",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "🐮", fontSize = 22.sp)
                            }
                        },
                        actions = {
                            IconButton(onClick = { isDarkMode = !isDarkMode }) {
                                Text(
                                    text = if (isDarkMode) "☀️" else "🌙",
                                    fontSize = 22.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        val items = listOf(
                            Triple("Dashboard", "📊", 0),
                            Triple("Historial", "📈", 1),
                            Triple("Ajustes", "⚙️", 2)
                        )

                        items.forEach { (title, iconEmoji, index) ->
                            val isSelected = (pagerState.currentPage == index)
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                icon = { Text(text = iconEmoji, fontSize = 20.sp) },
                                label = { Text(title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> DashboardEstabloScreen()
                            1 -> HistorialScreen(
                                ventilationOpen = ventilationOpen,
                                onVentilationChange = { ventilationOpen = it },
                                lightIntensity = lightIntensity,
                                onLightIntensityChange = { lightIntensity = it }
                            )
                            2 -> AjustesScreen(
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { isDarkMode = it },
                                onLogout = { isLoggedIn = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS MODO CLARO Y MODO OSCURO
// ==========================================

@Preview(showBackground = true, name = "Modo Claro")
@Composable
fun MainAppScaffoldPreviewLight() {
    MainAppScaffold()
}

@Preview(showBackground = true, name = "Modo Oscuro")
@Composable
fun MainAppScaffoldPreviewDark() {
    CuidadoVacasTheme(darkTheme = true) {
        MainAppScaffold()
    }
}