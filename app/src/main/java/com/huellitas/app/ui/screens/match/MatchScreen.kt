package com.huellitas.app.ui.screens.match

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huellitas.app.ui.theme.CremaBg
import com.huellitas.app.ui.theme.GrisTexto
import com.huellitas.app.ui.theme.NaranjaHuellitas

import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Pregunta(
    val texto: String,
    val opciones: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onIrACatalogo: () -> Unit = {}
) {
    var mostrarTest by remember { mutableStateOf(false) }
    var preguntaActualIndex by remember { mutableStateOf(0) }
    val respuestas = remember { mutableStateListOf<Int>() }

    val preguntas = listOf(
        Pregunta("¿Cuánto tiempo tienes para paseos diarios?", listOf("30 min", "1 hora", "Más de 2 horas")),
        Pregunta("¿Cómo es tu vivienda?", listOf("Departamento pequeño", "Casa con patio", "Campo o Granja")),
        Pregunta("¿Vives con niños o personas mayores?", listOf("Sí", "No")),
        Pregunta("¿Qué nivel de energía buscas en tu mascota?", listOf("Tranquilo", "Activo", "Muy juguetón"))
    )

    BackHandler {
        if (mostrarTest) {
            if (preguntaActualIndex > 0) {
                preguntaActualIndex--
            } else {
                mostrarTest = false
            }
        } else {
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!mostrarTest) {
                            Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF8D4934))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "HuellitasMatch",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D4934)
                            )
                        } else {
                            Text("Test de Vida", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    if (mostrarTest) {
                        IconButton(onClick = {
                            if (preguntaActualIndex > 0) preguntaActualIndex-- else mostrarTest = false
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                        }
                    }
                },
                actions = {
                    if (!mostrarTest) {
                        IconButton(onClick = { /* Notificaciones */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE7E1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D4934)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (!mostrarTest) {
            PantallaInicioMatch(padding) { mostrarTest = true }
        } else {
            if (preguntaActualIndex < preguntas.size) {
                PantallaPregunta(
                    padding = padding,
                    pregunta = preguntas[preguntaActualIndex],
                    progreso = (preguntaActualIndex + 1).toFloat() / preguntas.size,
                    onOpcionSeleccionada = { index ->
                        respuestas.add(index)
                        preguntaActualIndex++
                    }
                )
            } else {
                PantallaResultado(
                    padding = padding,
                    respuestas = respuestas,
                    onFinalizar = onVolver,
                    onIrACatalogo = onIrACatalogo
                )
            }
        }
    }
}

@Composable
fun PantallaInicioMatch(padding: PaddingValues, onComenzar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(CremaBg)
        ) {
            Icon(
                Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center),
                tint = NaranjaHuellitas.copy(alpha = 0.3f)
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            "Encuentra a tu compañero ideal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8D4934),
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Nuestro test de estilo de vida utiliza inteligencia animal para conectarte con la mascota que mejor se adapta a tu energía y hogar.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onComenzar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934))
        ) {
            Text("Comenzar Test de Vida", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun PantallaPregunta(
    padding: PaddingValues,
    pregunta: Pregunta,
    progreso: Float,
    onOpcionSeleccionada: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp)
    ) {
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF8D4934),
            trackColor = CremaBg
        )

        Spacer(Modifier.height(48.dp))

        Text(
            pregunta.texto,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        Spacer(Modifier.height(32.dp))

        pregunta.opciones.forEachIndexed { index, opcion ->
            OutlinedButton(
                onClick = { onOpcionSeleccionada(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8D4934))
            ) {
                Text(opcion, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun PantallaResultado(
    padding: PaddingValues,
    respuestas: List<Int>,
    onFinalizar: () -> Unit,
    onIrACatalogo: () -> Unit
) {
    val repo = remember { com.huellitas.app.data.repository.HuellitasRepository() }
    var perrosMatch by remember { mutableStateOf<List<com.huellitas.app.data.model.PerroAdopcion>>(emptyList()) }
    var otrosPerros by remember { mutableStateOf<List<com.huellitas.app.data.model.PerroAdopcion>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val todos = repo.obtenerPerrosDisponibles()
        
        // Lógica de Match Inteligente mejorada
        val scoredDogs = todos.map { perro ->
            var puntos = 0
            
            // Filtro de Energía (Pregunta 3)
            val buscaTranquilo = respuestas.getOrNull(3) == 0
            val buscaActivo = respuestas.getOrNull(3) == 1
            val buscaMuyJugueton = respuestas.getOrNull(3) == 2
            
            if (buscaTranquilo && perro.energia == "Baja") puntos += 4
            if (buscaActivo && perro.energia == "Media") puntos += 4
            if (buscaMuyJugueton && perro.energia == "Alta") puntos += 4
            
            // Filtro de Tamaño/Espacio (Pregunta 1)
            val viveEnDepto = respuestas.getOrNull(1) == 0
            val viveEnCasa = respuestas.getOrNull(1) == 1
            val viveEnGranja = respuestas.getOrNull(1) == 2

            if (viveEnDepto && perro.tamano == "Pequeño") puntos += 3
            if (viveEnCasa && (perro.tamano == "Mediano" || perro.tamano == "Pequeño")) puntos += 3
            if (viveEnGranja) puntos += 3 // Granja le va bien a cualquiera
            
            // Filtro de Niños (Pregunta 2)
            val tieneNinos = respuestas.getOrNull(2) == 0
            if (tieneNinos && (perro.descripcion.contains("niños", true) || perro.descripcion.contains("noble", true) || perro.descripcion.contains("dócil", true))) puntos += 2

            perro to puntos
        }

        perrosMatch = scoredDogs.filter { it.second >= 2 }.sortedByDescending { it.second }.map { it.first }
        
        if (perrosMatch.isEmpty()) {
            otrosPerros = todos.take(3)
        }

        cargando = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8D4934))
            }
        } else if (perrosMatch.isEmpty()) {
            Text("✨", fontSize = 64.sp)
            Text("¡Análisis completo!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
            Spacer(Modifier.height(16.dp))
            Text("No encontramos un match exacto ahora, pero estos amiguitos podrían gustarte:", textAlign = TextAlign.Center, color = Color.Gray)
            
            Spacer(Modifier.height(24.dp))
            
            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(otrosPerros) { perro ->
                    com.huellitas.app.ui.screens.main.TarjetaPerroDestacado(perro) { onIrACatalogo() }
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onIrACatalogo, 
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934))
            ) {
                Text("Explorar todo el catálogo")
            }
        } else {
            Text("🐾", fontSize = 48.sp)
            Text("¡Tus Matches Ideales!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
            Text("Basado en tu estilo de vida", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(Modifier.height(24.dp))

            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(perrosMatch.take(5)) { perro ->
                    com.huellitas.app.ui.screens.main.TarjetaPerroDestacado(perro) { onIrACatalogo() }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onIrACatalogo,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934))
            ) {
                Text("Ver más en el catálogo")
            }
        }
    }
}
