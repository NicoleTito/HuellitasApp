package com.huellitas.app.ui.screens.lost

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    onVolver: () -> Unit,
    onNavigateToCatalogo: () -> Unit = {},
    onNavigateToCircular: () -> Unit = {},
    onNavigateToImpacto: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    var perrosPerdidos by remember { mutableStateOf(emptyList<PerroPerdido>()) }
    var searchTexto by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        cargando = true
        perrosPerdidos = repo.obtenerPerrosPerdidos()
        cargando = false
    }

    BackHandler { onVolver() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Mascotas Perdidas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Ayúdalos a volver a casa", fontSize = 12.sp, color = GrisSecundario)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { perrosPerdidos = repo.obtenerPerrosPerdidos() } }) {
                        Icon(Icons.Default.Refresh, null, tint = NaranjaHuellitas)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = RojoError,
                contentColor = BlancoPuro,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Campaign, null) },
                text = { Text("Reportar Extravío", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BlancoPuro,
                tonalElevation = 8.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCatalogo,
                    icon = { Icon(Icons.Default.Pets, null) },
                    label = { Text("Catálogo", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.Announcement, null) },
                    label = { Text("Perdidos", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCircular,
                    icon = { Icon(Icons.Default.Eco, null) },
                    label = { Text("Circular", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToImpacto,
                    icon = { Icon(Icons.Default.PersonOutline, null) },
                    label = { Text("Impacto", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
                    )
                )
            }
        },
        containerColor = CremaBg
    ) { padding ->
        val filtrados = perrosPerdidos.filter { 
            it.nombre.contains(searchTexto, true) || it.zona.contains(searchTexto, true) || it.raza.contains(searchTexto, true)
        }

        Column(modifier = Modifier.padding(padding)) {
            // Buscador
            OutlinedTextField(
                value = searchTexto,
                onValueChange = { searchTexto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por nombre, raza o zona...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = GrisSecundario) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BlancoPuro,
                    unfocusedContainerColor = BlancoPuro,
                    focusedBorderColor = NaranjaHuellitas.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            if (cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NaranjaHuellitas)
                }
            } else if (filtrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Text("🔍", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Sin reportes que coincidan", fontWeight = FontWeight.Bold, color = GrisTexto)
                        Text("Intenta con otros términos o reporta una mascota si la perdiste.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = GrisSecundario, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtrados) { perro ->
                        TarjetaPerroPerdido(perro)
                    }
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
                        cargando = true
                        perrosPerdidos = repo.obtenerPerrosPerdidos()
                        cargando = false
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                if (perro.fotoUri.isNotEmpty()) {
                    AsyncImage(
                        model = perro.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(NaranjaClaro), contentAlignment = Alignment.Center) {
                        Text(if(perro.especie.lowercase() == "gato") "🐱" else "🐶", fontSize = 60.sp)
                    }
                }
                
                // Etiqueta de "PERDIDO"
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    color = RojoError,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "PERDIDO",
                        color = BlancoPuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(perro.nombre.ifBlank { "Sin nombre" }, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = GrisTexto)
                    Text(perro.fechaPerdido, fontSize = 12.sp, color = GrisSecundario)
                }
                
                Text("${perro.especie} • ${perro.raza} • ${perro.color}", fontSize = 14.sp, color = GrisSecundario)
                
                if (perro.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(perro.descripcion, fontSize = 13.sp, color = GrisTexto, maxLines = 2)
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = RojoError, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(perro.zona, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GrisTexto)
                }
                
                Spacer(Modifier.height(12.dp))
                
                Button(
                    onClick = { /* Implementar llamada o WhatsApp */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeExito)
                ) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Contactar: ${perro.contacto}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoReportarPerdido(usuarioId: String, onDismiss: () -> Unit, onReportado: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("Perro") }
    var raza by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("Mediano") }
    var color by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    
    var subiendo by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imagenUri = it
    }

    AlertDialog(
        onDismissRequest = if (subiendo) ({}) else onDismiss,
        title = { Text("📢 Reportar Mascota Perdida", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    // Selector de imagen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F0F2))
                            .clickable { if (!subiendo) picker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenUri != null) {
                            AsyncImage(
                                model = imagenUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, tint = GrisSecundario)
                                Text("Añadir foto del perro", fontSize = 12.sp, color = GrisSecundario)
                            }
                        }
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = nombre, 
                        onValueChange = { nombre = it }, 
                        label = { Text("Nombre de la mascota") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    Text("Especie", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Perro", "Gato", "Otro").forEach { esp ->
                            FilterChip(
                                selected = especie == esp,
                                onClick = { especie = esp },
                                label = { Text(esp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = raza, 
                        onValueChange = { raza = it }, 
                        label = { Text("Raza / Mezcla") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = color, 
                        onValueChange = { color = it }, 
                        label = { Text("Color y señas particulares") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = zona, 
                        onValueChange = { zona = it }, 
                        label = { Text("¿Dónde y cuándo se perdió?") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Ej: Col. Roma, cerca del parque") }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = descripcion, 
                        onValueChange = { descripcion = it }, 
                        label = { Text("Información adicional") }, 
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = contacto, 
                        onValueChange = { contacto = it }, 
                        label = { Text("Teléfono de contacto") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = VerdeExito, modifier = Modifier.size(20.dp)) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (contacto.isNotBlank() && zona.isNotBlank()) {
                        scope.launch {
                            subiendo = true
                            
                            val urlFoto = if (imagenUri != null) {
                                repo.subirImagen(imagenUri!!, "lost_pets") ?: ""
                            } else ""

                            val nuevo = PerroPerdido(
                                nombre = nombre,
                                especie = especie,
                                raza = raza,
                                tamano = tamano,
                                color = color,
                                zona = zona,
                                descripcion = descripcion,
                                contacto = contacto,
                                fotoUri = urlFoto,
                                reporteroId = usuarioId
                            )
                            repo.reportarPerroPerdido(nuevo)
                            subiendo = false
                            onReportado()
                        }
                    }
                },
                enabled = !subiendo,
                colors = ButtonDefaults.buttonColors(containerColor = RojoError),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (subiendo) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BlancoPuro,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Subiendo...")
                } else {
                    Text("Publicar Alerta", color = BlancoPuro)
                }
            }
        },
        dismissButton = {
            if (!subiendo) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}
