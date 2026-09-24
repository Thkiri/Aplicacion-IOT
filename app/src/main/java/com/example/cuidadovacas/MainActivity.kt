package com.example.cuidadovacas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cuidadovacas.screens.DashboardEstabloScreen
import com.example.cuidadovacas.ui.theme.CuidadoVacasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CuidadoVacasTheme {
                DashboardEstabloScreen()
            }
        }
    }
}