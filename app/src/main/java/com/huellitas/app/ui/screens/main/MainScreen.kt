package com.huellitas.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    usuario: Usuario,
    onIrAPerfil: () -> Unit,
    onIrAAdopciones: () -> Unit,
    onIrAMapa: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        containerColor = CremaBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Huellitas", fontWeight = FontWeight.Bold, color = NaranjaHuellitas, fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onIrAPerfil) {
                        Box(
                            modifier         = Modifier
                                .size(34.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(NaranjaHuellitas),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                color      = BlancoPuro,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremaBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(10.dp))

            // Saludo
            Text(
                "¡Hola, ${usuario.nombre.split(" ").first()}! ",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = GrisTexto
            )
            Text("¿Qué quieres hacer hoy?", fontSize = 14.sp, color = GrisSecundario)

            Spacer(Modifier.height(24.dp))

            // Tarjetas de acción principal
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAccion(
                    emoji    = "🐾",
                    titulo   = "Adoptar",
                    subtitulo = "Encuentra tu compañero ideal",
                    modifier  = Modifier.weight(1f),
                    onClick   = onIrAAdopciones
                )
                TarjetaAccion(
                    emoji    = "👤",
                    titulo   = "Mi perfil",
                    subtitulo = "Ver mis solicitudes",
                    modifier  = Modifier.weight(1f),
                    onClick   = onIrAPerfil
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAccion(
                    emoji     = "📢",
                    titulo    = "Perro perdido",
                    subtitulo = "Reportar o buscar",
                    modifier  = Modifier.weight(1f),
                    onClick   = { /* Próximamente */ }
                )
                TarjetaAccion(
                    emoji     = "📍",
                    titulo    = "Albergues",
                    subtitulo = "Ver en el mapa",
                    modifier  = Modifier.weight(1f),
                    onClick   = onIrAMapa
                )
            }

            Spacer(Modifier.height(24.dp))

            // Banner informativo
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NaranjaHuellitas)
            ) {
                Row(
                    modifier          = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "¿Sabías que...?",
                            fontWeight = FontWeight.Bold,
                            color      = BlancoPuro,
                            fontSize   = 15.sp
                        )
                        Text(
                            "En Perú hay más de 6.5 millones de perros en situación de calle. Adoptar salva vidas. 🐾",
                            color    = BlancoPuro.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("", fontSize = 42.sp)
                }
            }
        }
    }
}

@Composable
fun TarjetaAccion(
    emoji: String,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(3.dp),
        onClick   = onClick
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.height(6.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto, textAlign = TextAlign.Center)
            Text(subtitulo, fontSize = 11.sp, color = GrisSecundario, textAlign = TextAlign.Center)
        }
    }
}

