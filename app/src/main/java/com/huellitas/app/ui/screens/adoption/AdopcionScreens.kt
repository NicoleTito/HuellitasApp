package com.huellitas.app.ui.screens.adoption

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.theme.*
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════
//  LISTADO DE ADOPCIONES (VISTA CATÁLOGO PREMIUM)
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdopcionListScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVerDetalle: (PerroAdopcion) -> Unit,
    onNavigateToMatch: () -> Unit = {},
    onNavigateToCircular: () -> Unit = {},
    onNavigateToImpacto: () -> Unit = {},
    onNavigateToPublicar: () -> Unit = {},
    onNavigateToPerdidos: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    var perros by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var searchTexto by remember { mutableStateOf("") }
    var filtroEspecie by remember { mutableStateOf("Todos") }
    var filtroTamano by remember { mutableStateOf("Todos") }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        cargando = true
        perros = repo.obtenerPerrosDisponibles()
        cargando = false
    }

    Scaffold(
        containerColor = BlancoPuro,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, null, tint = Color(0xFF8D4934), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("HuellasHogar", fontWeight = FontWeight.ExtraBold, color = Color(0xFF8D4934), fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.LocationOn, null, tint = GrisSecundario, modifier = Modifier.size(14.dp))
                        Text("CDMX", fontSize = 12.sp, color = GrisSecundario, fontWeight = FontWeight.Medium)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.NotificationsNone, null, tint = GrisTexto)
                    }
                    Box(
                        modifier = Modifier.padding(end = 16.dp).size(36.dp).clip(CircleShape).background(Color(0xFFFDE7E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D4934)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        floatingActionButton = {
            // LÓGICA DE ROLES: Solo los albergues pueden subir perros en adopción
            if (usuario.rol == "albergue") {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToPublicar,
                    containerColor = Color(0xFFE67E5D),
                    contentColor = BlancoPuro,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Publicar Huellita", fontWeight = FontWeight.Bold) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = BlancoPuro,
                tonalElevation = 8.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
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
                    selected = false,
                    onClick = onNavigateToMatch,
                    icon = { Icon(Icons.Default.FavoriteBorder, null) },
                    label = { Text("Match", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToPerdidos,
                    icon = { Icon(Icons.Default.Announcement, null) },
                    label = { Text("Perdidos", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCircular,
                    icon = { Icon(Icons.Default.Eco, null) },
                    label = { Text("Circular", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToImpacto,
                    icon = { Icon(Icons.Default.PersonOutline, null) },
                    label = { Text("Impacto", fontSize = 10.sp) }
                )
            }
        }
    ) { padding ->
        val perrosFiltrados = perros.filter {
            val matchesSearch = it.nombre.contains(searchTexto, ignoreCase = true) ||
                    it.raza.contains(searchTexto, ignoreCase = true) ||
                    it.descripcion.contains(searchTexto, ignoreCase = true) ||
                    it.zona.contains(searchTexto, ignoreCase = true)
            
            val matchesEspecie = filtroEspecie == "Todos" || it.especie.equals(filtroEspecie, ignoreCase = true)
            val matchesTamano = filtroTamano == "Todos" || it.tamano.equals(filtroTamano, ignoreCase = true)
            
            matchesSearch && matchesEspecie && matchesTamano
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchTexto,
                    onValueChange = { searchTexto = it },
                    placeholder = { Text("Busca tu nuevo mejor amigo...", fontSize = 14.sp, color = GrisSecundario) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = GrisSecundario) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF5F5F7),
                        unfocusedContainerColor = Color(0xFFF5F5F7)
                    ),
                    singleLine = true
                )
            }

            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Fila de Especies
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            FilterChip(
                                selected = filtroEspecie == "Todos",
                                onClick = { filtroEspecie = "Todos" },
                                label = { Text("Todos") },
                                leadingIcon = if (filtroEspecie == "Todos") { { Icon(Icons.Default.Done, null, modifier = Modifier.size(18.dp)) } } else null,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        items(listOf("Perro", "Gato", "Otro")) { especie ->
                            FilterChip(
                                selected = filtroEspecie == especie,
                                onClick = { filtroEspecie = especie },
                                label = { Text(especie) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE67E5D),
                                    selectedLabelColor = BlancoPuro
                                )
                            )
                        }
                    }
                    // Fila de Tamaños
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item { Text("Tamaño:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrisSecundario) }
                        items(listOf("Pequeño", "Mediano", "Grande")) { tam ->
                            FilterChip(
                                selected = filtroTamano == tam,
                                onClick = { filtroTamano = if (filtroTamano == tam) "Todos" else tam },
                                label = { Text(tam, fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            if (cargando) {
                items(6) { SkeletonMascota() }
            } else {
                items(perrosFiltrados) { perro ->
                    TarjetaMascotaGrid(perro) { onVerDetalle(perro) }
                }
            }

            item(span = { GridItemSpan(2) }) { BannerHistorias() }
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun TarjetaMascotaGrid(perro: PerroAdopcion, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                if (perro.fotoUri.isNotEmpty()) {
                    AsyncImage(
                        model = perro.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(NaranjaClaro), contentAlignment = Alignment.Center) {
                        val emoji = when(perro.especie.lowercase()) {
                            "gato" -> "🐱"
                            "perro" -> "🐶"
                            else -> "🐾"
                        }
                        Text(emoji, fontSize = 40.sp)
                    }
                }
                Box(
                    modifier = Modifier.padding(8.dp).size(32.dp).align(Alignment.TopEnd).clip(CircleShape).background(BlancoPuro.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(18.dp), tint = GrisTexto)
                }
            }
            Column(Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(perro.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = GrisTexto)
                    Surface(color = Color(0xFFFDE7E1), shape = RoundedCornerShape(8.dp)) {
                        Text(perro.edad, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E5D), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(perro.zona, fontSize = 12.sp, color = GrisSecundario, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun BannerHistorias() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7E5A4))
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp),
                tint = Color(0xFF5A6632).copy(alpha = 0.1f)
            )
            Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                Text("Adopta, no compres", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF5A6632))
                Spacer(Modifier.height(8.dp))
                Text("Dale una segunda oportunidad a un amigo que te espera.", fontSize = 14.sp, color = Color(0xFF5A6632).copy(alpha = 0.8f))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("Ver historias", color = BlancoPuro, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAdopcionScreen(perro: PerroAdopcion, usuario: com.huellitas.app.data.model.Usuario, onVolver: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }
    var enviado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(perro.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        containerColor = CremaBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(18.dp)).background(NaranjaClaro),
                    contentAlignment = Alignment.Center
                ) {
                    if (perro.fotoUri.isNotBlank()) {
                        AsyncImage(model = perro.fotoUri, contentDescription = perro.nombre, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text("🐶", fontSize = 72.sp)
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(perro.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                    ChipEstado(perro.estado)
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = BlancoPuro)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Información", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                        Spacer(Modifier.height(10.dp))
                        FilaInfo("Especie", perro.especie)
                        FilaInfo("Raza", perro.raza)
                        FilaInfo("Tamaño", perro.tamano)
                        FilaInfo("Color", perro.color)
                        FilaInfo("Edad", perro.edad)
                        FilaInfo("Zona", perro.zona)
                        FilaInfo("Albergue", perro.albergueNombre)
                    }
                }
            }

            if (perro.descripcion.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = BlancoPuro)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Sobre ${perro.nombre}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                            Spacer(Modifier.height(8.dp))
                            Text(perro.descripcion, color = GrisSecundario, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }

            // RESTRICCIÓN: Solo usuarios que NO son albergues pueden solicitar adopción
            if (usuario.rol != "albergue" && perro.estado == "disponible" && !enviado) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = BlancoPuro)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Solicitar adopción", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = mensaje,
                                onValueChange = { mensaje = it },
                                label = { Text("¿Por qué quieres adoptar?") },
                                modifier = Modifier.fillMaxWidth().height(110.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            if (resultado.isNotBlank()) {
                                Text(resultado, color = if (isError) RojoError else VerdeExito, fontSize = 13.sp)
                                Spacer(Modifier.height(10.dp))
                            }
                            Button(
                                onClick = {
                                    if (mensaje.isBlank()) {
                                        resultado = "Escribe un mensaje"
                                        isError = true
                                    } else {
                                        scope.launch {
                                            enviando = true
                                            val sol = SolicitudAdopcion(perroId = perro.id, usuarioId = usuario.id, mensaje = mensaje)
                                            val res = repo.crearSolicitud(sol)
                                            enviando = false
                                            res.onSuccess { resultado = "¡Solicitud enviada!"; isError = false; enviado = true }
                                            res.onFailure { resultado = it.message ?: "Error"; isError = true }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !enviando,
                                colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas)
                            ) { Text(if (enviando) "Enviando..." else "Enviar solicitud", color = BlancoPuro) }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun FilaInfo(etiqueta: String, valor: String) {
    if (valor.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = GrisSecundario, fontSize = 14.sp)
        Text(valor, color = GrisTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChipEstado(estado: String) {
    val color = when(estado) {
        "disponible" -> VerdeExito
        "en_proceso" -> AmarilloAlerta
        else -> GrisSecundario
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(estado.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarPerroScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("Perro") }
    var raza by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("Mediano") }
    var edad by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var subiendo by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var exito by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imagenUri = it
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Huellita", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        containerColor = CremaBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF0F0F2))
                        .clickable { picker.launch("image/*") },
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
                            Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(48.dp), tint = GrisSecundario)
                            Spacer(Modifier.height(8.dp))
                            Text("Añadir foto", color = GrisSecundario, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                CampoFormulario("Nombre", nombre, { nombre = it }, Icons.Default.Pets)
            }

            item {
                Text("Especie", fontWeight = FontWeight.Bold, color = GrisTexto)
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
                CampoFormulario("Raza", raza, { raza = it }, Icons.Default.Category)
            }

            item {
                Text("Tamaño", fontWeight = FontWeight.Bold, color = GrisTexto)
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        CampoFormulario("Edad", edad, { edad = it }, Icons.Default.History)
                    }
                    Box(Modifier.weight(1f)) {
                        CampoFormulario("Color", color, { color = it }, Icons.Default.Palette)
                    }
                }
            }

            item {
                CampoFormulario("Zona/Ubicación", zona, { zona = it }, Icons.Default.LocationOn)
            }

            item {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción / Historia") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlancoPuro,
                        unfocusedContainerColor = BlancoPuro
                    )
                )
            }

            if (mensaje.isNotBlank()) {
                item {
                    Text(mensaje, color = if (exito) VerdeExito else RojoError, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Button(
                    onClick = {
                        if (nombre.isBlank() || raza.isBlank() || zona.isBlank()) {
                            mensaje = "Por favor completa los campos obligatorios"
                            exito = false
                        } else {
                            scope.launch {
                                subiendo = true
                                mensaje = "Publicando..."
                                
                                val fotoUrl = if (imagenUri != null) {
                                    repo.subirImagen(imagenUri!!, "pets") ?: "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500"
                                } else {
                                    "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500"
                                }

                                val nuevoPerro = PerroAdopcion(
                                    nombre = nombre,
                                    especie = especie,
                                    raza = raza,
                                    tamano = tamano,
                                    edad = edad,
                                    color = color,
                                    zona = zona,
                                    descripcion = descripcion,
                                    fotoUri = fotoUrl,
                                    albergueId = usuario.id,
                                    albergueNombre = usuario.nombre
                                )
                                
                                repo.agregarPerro(nuevoPerro)
                                subiendo = false
                                exito = true
                                mensaje = "¡Huellita publicada con éxito!"
                                // Podríamos navegar atrás después de un delay
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas),
                    enabled = !subiendo
                ) {
                    if (subiendo) {
                        CircularProgressIndicator(color = BlancoPuro, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Publicar para Adopción", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun CampoFormulario(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Color(0xFFE67E5D), modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BlancoPuro,
            unfocusedContainerColor = BlancoPuro
        )
    )
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(Color.LightGray.copy(alpha = 0.6f), Color.LightGray.copy(alpha = 0.2f), Color.LightGray.copy(alpha = 0.6f))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmer"
    )
    return Brush.linearGradient(colors = shimmerColors, start = Offset.Zero, end = Offset(x = translateAnim.value, y = translateAnim.value))
}

@Composable
fun SkeletonMascota() {
    val brush = rememberShimmerBrush()
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = BlancoPuro), elevation = CardDefaults.cardElevation(1.dp)) {
        Column {
            Box(modifier = Modifier.height(180.dp).fillMaxWidth().background(brush))
            Column(Modifier.padding(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).background(brush))
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).background(brush))
            }
        }
    }
}
