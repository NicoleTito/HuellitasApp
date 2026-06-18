package com.huellitas.app.ui.screens.lost

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huellitas.app.data.model.PerroPerdido
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerrosPerdidosScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit
) {
    val repo = remember { HuellitasRepository() }
    val scope = rememberCoroutineScope()
    var perrosPerdidos by remember { mutableStateOf(emptyList<PerroPerdido>()) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        perrosPerdidos = repo.obtenerPerrosPerdidos()
    }

    BackHandler { onVolver() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perros Perdidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        floatingActionButton = {
            // Solo dueños y voluntarios pueden reportar mascotas perdidas
            if (usuario.rol != "albergue") {
                FloatingActionButton(
                    onClick = { mostrarDialogo = true },
                    containerColor = NaranjaHuellitas,
                    contentColor = BlancoPuro
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Reportar")
                }
            }
        },
        containerColor = CremaBg
    ) { padding ->
        if (perrosPerdidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📢", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No hay reportes activos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "Si perdiste a tu mascota, repórtalo aquí.",
                        modifier = Modifier.padding(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(perrosPerdidos) { perro ->
                    TarjetaPerroPerdido(perro)
                }
            }
        }

        if (mostrarDialogo) {
            DialogoReportarPerdido(
                usuarioId = usuario.id,
                onDismiss = { mostrarDialogo = false },
                onReportado = {
                    mostrarDialogo = false
                    scope.launch {
                        perrosPerdidos = repo.obtenerPerrosPerdidos()
                    }
                }
            )
        }
    }
}

@Composable
fun TarjetaPerroPerdido(perro: PerroPerdido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaranjaClaro)
            ) {
                if (perro.fotoUri.isNotEmpty()) {
                    AsyncImage(
                        model = perro.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val emoji = when(perro.especie.lowercase()) {
                        "gato" -> "🐱"
                        "perro" -> "🐶"
                        else -> "🐾"
                    }
                    Text(emoji, modifier = Modifier.align(Alignment.Center), fontSize = 32.sp)
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RojoError)
                Text("${perro.especie} • ${perro.raza} (${perro.tamano})", fontSize = 13.sp, color = GrisSecundario)
                Text("Color: ${perro.color}", fontSize = 12.sp, color = GrisSecundario)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = NaranjaHuellitas, modifier = Modifier.size(16.dp))
                    Text(perro.zona, fontSize = 12.sp, color = GrisTexto)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = VerdeExito, modifier = Modifier.size(16.dp))
                    Text(perro.contacto, fontSize = 12.sp, color = GrisTexto)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoReportarPerdido(usuarioId: String, onDismiss: () -> Unit, onReportado: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("Perro") }
    var raza by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("Mediano") }
    var color by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    val repo = remember { HuellitasRepository() }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Mascota Perdida") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text("Especie", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Perro", "Gato", "Otro").forEach { esp ->
                            FilterChip(
                                selected = especie == esp,
                                onClick = { especie = esp },
                                label = { Text(esp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text("Tamaño", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Pequeño", "Mediano", "Grande").forEach { tam ->
                            FilterChip(
                                selected = tamano == tam,
                                onClick = { tamano = tam },
                                label = { Text(tam) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = zona, onValueChange = { zona = it }, label = { Text("¿Dónde se perdió?") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = contacto, onValueChange = { contacto = it }, label = { Text("Teléfono de contacto") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && contacto.isNotBlank()) {
                        scope.launch {
                            val nuevo = PerroPerdido(
                                nombre = nombre,
                                especie = especie,
                                raza = raza,
                                tamano = tamano,
                                color = color,
                                zona = zona,
                                contacto = contacto,
                                reporteroId = usuarioId
                            )
                            repo.reportarPerroPerdido(nuevo)
                            onReportado()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RojoError)
            ) {
                Text("Publicar Reporte", color = BlancoPuro)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
