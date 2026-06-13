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
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = NaranjaHuellitas,
                contentColor = BlancoPuro
            ) {
                Icon(Icons.Default.Add, contentDescription = "Reportar")
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
                    Text("🐶", modifier = Modifier.align(Alignment.Center), fontSize = 32.sp)
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RojoError)
                Text("${perro.raza} • ${perro.color}", fontSize = 14.sp, color = GrisSecundario)
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
fun DialogoReportarPerdido(onDismiss: () -> Unit, onReportado: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    val repo = remember { HuellitasRepository() }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Perro Perdido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") })
                OutlinedTextField(value = zona, onValueChange = { zona = it }, label = { Text("Zona/Lugar") })
                OutlinedTextField(value = contacto, onValueChange = { contacto = it }, label = { Text("Teléfono de contacto") })
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    val nuevo = PerroPerdido(
                        nombre = nombre,
                        raza = raza,
                        zona = zona,
                        contacto = contacto
                    )
                    repo.reportarPerroPerdido(nuevo)
                    onReportado()
                }
            }) {
                Text("Reportar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
