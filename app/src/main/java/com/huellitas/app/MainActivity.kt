package com.huellitas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.huellitas.app.navigation.HuellitasNavHost
import com.huellitas.app.ui.theme.HuellitasTheme
import com.huellitas.app.data.repository.HuellitasRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar repositorio con el contexto para Cloudinary
        HuellitasRepository(this)

        enableEdgeToEdge()
        setContent {
            HuellitasTheme {
                HuellitasNavHost()
            }
        }
    }
}