package com.huellitas.app.ui.screens.albergue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.components.*
import com.huellitas.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAlbergueScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    onEditarMascota: (PerroAdopcion) -> Unit
) {
    val repo = remember { HuellitasRepository() }
    val scope = rememberCoroutineScope()
    var tabSelected by remember { mutableStateOf(0) } // 0: Mascotas, 1: Solicitudes
    
    var misMascotas by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var solicitudesRecibidas by remember { mutableStateOf(emptyList<SolicitudAdopcion>()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        cargando = true
        misMascotas = repo.obtenerMisMascotas(usuario.id)
        solicitudesRecibidas = repo.obtenerSolicitudesRecibidas(usuario.id)
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar Albergue", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        containerColor = CremaBg
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = tabSelected,
                containerColor = BlancoPuro,
                contentColor = NaranjaHuellitas,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabSelected]),
                        color = NaranjaHuellitas
                    )
                }
            ) {
                Tab(
                    selected = tabSelected == 0,
                    onClick = { tabSelected = 0 },
                    text = { Text("Mis Mascotas") }
                )
                Tab(
                    selected = tabSelected == 1,
                    onClick = { tabSelected = 1 },
                    text = { Text("Solicitudes (${solicitudesRecibidas.size})") }
                )
            }

            if (cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NaranjaHuellitas)
                }
            } else {
                when (tabSelected) {
                    0 -> ListaMisMascotas(
                        mascotas = misMascotas,
                        onEditar = onEditarMascota,
                        onDelete = { perro ->
                            scope.launch {
                                val ok = repo.borrarMascota(perro.id)
                                if (ok) misMascotas = repo.obtenerMisMascotas(usuario.id)
                            }
                        }
                    )
                    1 -> ListaSolicitudesRecibidas(
                        solicitudes = solicitudesRecibidas,
                        repo = repo,
                        onUpdate = {
                            scope.launch {
                                solicitudesRecibidas = repo.obtenerSolicitudesRecibidas(usuario.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ListaMisMascotas(
    mascotas: List<PerroAdopcion>,
    onEditar: (PerroAdopcion) -> Unit,
    onDelete: (PerroAdopcion) -> Unit
) {
    if (mascotas.isEmpty()) {
        MensajeVacio("No has publicado ninguna mascota aún.", "🐶")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mascotas) { perro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(NaranjaClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            if (perro.fotoUri.isNotBlank()) {
                                AsyncImage(model = perro.fotoUri, contentDescription = null, contentScale = ContentScale.Crop)
                            } else {
                                Text("🐾")
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(perro.estado.uppercase(), fontSize = 12.sp, color = NaranjaHuellitas, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { onEditar(perro) }) {
                            Icon(Icons.Default.Edit, null, tint = GrisSecundario)
                        }
                        IconButton(onClick = { onDelete(perro) }) {
                            Icon(Icons.Default.Delete, null, tint = RojoError)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListaSolicitudesRecibidas(
    solicitudes: List<SolicitudAdopcion>,
    repo: HuellitasRepository,
    onUpdate: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    if (solicitudes.isEmpty()) {
        MensajeVacio("No has recibido solicitudes aún.", "📩")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(solicitudes) { sol ->
                var usuarioData by remember { mutableStateOf<Usuario?>(null) }
                var perroData by remember { mutableStateOf<PerroAdopcion?>(null) }
                
                LaunchedEffect(sol.usuarioId) {
                    usuarioData = repo.obtenerUsuarioPorId(sol.usuarioId)
                    // Para obtener el nombre del perro (simplificado)
                    val perros = repo.obtenerPerrosDisponibles()
                    perroData = perros.find { it.id == sol.perroId }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).clip(CircleShape).background(NaranjaHuellitas), contentAlignment = Alignment.Center) {
                                Text(usuarioData?.nombre?.firstOrNull()?.toString() ?: "?", color = BlancoPuro)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(usuarioData?.nombre ?: "Usuario", fontWeight = FontWeight.Bold)
                                Text("Interesado en: ${perroData?.nombre ?: "Cargando..."}", fontSize = 13.sp, color = GrisSecundario)
                            }
                            Spacer(Modifier.weight(1f))
                            ChipEstado(sol.estado)
                        }
                        
                        Spacer(Modifier.height(10.dp))
                        Text(sol.mensaje, fontSize = 14.sp, color = GrisTexto)
                        Spacer(Modifier.height(12.dp))
                        
                        if (sol.estado == "pendiente") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            repo.actualizarEstadoSolicitud(sol.id, "aprobada")
                                            onUpdate()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdeExito),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Aprobar") }
                                
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            repo.actualizarEstadoSolicitud(sol.id, "rechazada")
                                            onUpdate()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Rechazar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
