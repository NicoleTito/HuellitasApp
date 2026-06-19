package com.huellitas.app.ui.screens.adoption

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
    onNavigateToPerdidos: () -> Unit = {},
    onVerHistorias: () -> Unit = {}
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
                    onClick = onNavigateToPerdidos,
                    icon = { Icon(Icons.Default.Announcement, null) },
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

            item(span = { GridItemSpan(2) }) { BannerHistorias(onVerHistorias) }
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
fun BannerHistorias(onVerHistorias: () -> Unit) {
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
                    onClick = onVerHistorias,
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
fun DetalleAdopcionScreen(
    perro: PerroAdopcion,
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onIrAFormulario: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    
    var albergue by remember { mutableStateOf<com.huellitas.app.data.model.Usuario?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }
    var enviado by remember { mutableStateOf(false) }

    LaunchedEffect(perro.albergueId) {
        albergue = repo.obtenerUsuarioPorId(perro.albergueId)
    }

    Scaffold(
        containerColor = BlancoPuro,
        bottomBar = {
            if (usuario.rol != "albergue" && perro.estado == "disponible" && !enviado) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = BlancoPuro
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onIrAFormulario,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934))
                        ) {
                            Icon(Icons.Default.VolunteerActivism, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Solicitar Adopción", fontWeight = FontWeight.Bold)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                val tel = albergue?.telefono
                                if (!tel.isNullOrBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://api.whatsapp.com/send?phone=$tel&text=Hola, te escribo desde HuellitasApp. Estoy interesado en adoptar a ${perro.nombre} 🐾")
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF8D4934)))
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, null, tint = Color(0xFF8D4934), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Chat con albergue", color = Color(0xFF8D4934), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // IMAGEN CON BOTONES OVERLAY
            item {
                Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                    if (perro.fotoUri.isNotBlank()) {
                        AsyncImage(
                            model = perro.fotoUri,
                            contentDescription = perro.nombre,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.fillMaxSize().background(NaranjaClaro).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)), contentAlignment = Alignment.Center) {
                            Text("🐶", fontSize = 100.sp)
                        }
                    }
                    
                    // Botones superiores
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onVolver,
                            modifier = Modifier.size(40.dp).background(BlancoPuro.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = GrisTexto)
                        }
                        IconButton(
                            onClick = { /* Like */ },
                            modifier = Modifier.size(40.dp).background(BlancoPuro.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Default.FavoriteBorder, "Favorito", tint = Color.Red)
                        }
                    }
                }
            }

            // NOMBRE Y UBICACIÓN
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(perro.nombre, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = GrisTexto)
                        Surface(color = Color(0xFFD7E5A4), shape = RoundedCornerShape(12.dp)) {
                            Text(
                                perro.edad,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5A6632)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = NaranjaHuellitas, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(perro.zona, color = GrisSecundario, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // GRID DE ATRIBUTOS
            item {
                Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ItemAtributo(label = "RAZA", valor = perro.raza, icono = Icons.Default.Pets, modifier = Modifier.weight(1f))
                    ItemAtributo(label = "SEXO", valor = perro.sexo, icono = Icons.Default.Transgender, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ItemAtributo(label = "TAMAÑO", valor = perro.tamano, icono = Icons.Default.Straighten, modifier = Modifier.weight(1f))
                    ItemAtributo(label = "ENERGÍA", valor = perro.energia, icono = Icons.Default.FlashOn, modifier = Modifier.weight(1f))
                }
            }

            // SOBRE MÍ
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Sobre mí", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrisTexto)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        perro.descripcion,
                        color = GrisSecundario,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                }
            }

            // ESTADO DE SALUD
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("Estado de Salud", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrisTexto)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipSalud("Vacunas al día", Icons.Default.Shield)
                        ChipSalud("Desparasitado", Icons.Default.AddModerator)
                    }
                }
            }

            // ALBERGUE CARD
            item {
                Card(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9ECEF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFDE7E1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HomeWork, null, tint = Color(0xFFE67E5D))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ALBERGUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisSecundario)
                            Text(perro.albergueNombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                        }
                        IconButton(onClick = { /* Mapa */ }) {
                            Icon(Icons.Default.Map, null, tint = GrisSecundario)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ItemAtributo(label: String, valor: String, icono: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, null, tint = Color(0xFF8D4934), modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisSecundario)
            Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
        }
    }
}

@Composable
fun ChipSalud(texto: String, icono: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = Color(0xFFF1F4E9),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, null, tint = Color(0xFF5A6632), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(texto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6632))
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
fun FormularioAdopcionScreen(
    perro: PerroAdopcion,
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onFinalizar: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    
    var paso by remember { mutableStateOf(1) }
    
    // Paso 1
    var nombreCompleto by remember { mutableStateOf(usuario.nombre) }
    var direccion by remember { mutableStateOf("") }
    var tipoVivienda by remember { mutableStateOf("") }
    var tieneOtrasMascotas by remember { mutableStateOf(false) }
    var infoOtrasMascotas by remember { mutableStateOf("") }
    
    // Paso 2 (Nuevos campos)
    var tiempoSola by remember { mutableStateOf("") }
    var responsable by remember { mutableStateOf("") }
    var presupuestoOk by remember { mutableStateOf(false) }
    
    // Paso 3
    var compromisoChecked by remember { mutableStateOf(false) }
    var firmaNombre by remember { mutableStateOf("") }
    
    var enviando by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Text(
                            if (paso == 1) "HuellasHogar" else "Solicitud de Adopción",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D4934),
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.Default.Close, null, tint = Color(0xFF8D4934))
                        }
                    },
                    actions = {
                        if (paso == 1) {
                            Box(modifier = Modifier.padding(end = 16.dp).size(32.dp).clip(CircleShape).background(Color(0xFFE67E5D))) {
                                Text(usuario.nombre.first().toString(), modifier = Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                "Paso $paso de 3",
                                modifier = Modifier.padding(end = 16.dp),
                                fontSize = 14.sp,
                                color = GrisSecundario
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                
                if (paso == 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("PASO 1 DE 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
                        Text("Información Personal", fontSize = 12.sp, color = GrisSecundario)
                    }
                }
                
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth().height(4.dp).background(Color.LightGray.copy(alpha = 0.3f), CircleShape)) {
                    Box(modifier = Modifier.fillMaxWidth(paso / 3f).fillMaxHeight().background(Color(0xFF8D4934), CircleShape))
                }
            }
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp, color = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { if(paso > 1) paso-- else onVolver() }) {
                        Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Atrás", color = GrisTexto)
                    }
                    
                    Button(
                        onClick = {
                            if (paso < 3) {
                                paso++
                            } else {
                                scope.launch {
                                    enviando = true
                                    val solicitud = SolicitudAdopcion(
                                        perroId = perro.id,
                                        usuarioId = usuario.id,
                                        nombreCompleto = nombreCompleto,
                                        direccion = direccion,
                                        tipoVivienda = tipoVivienda,
                                        tieneMascotas = tieneOtrasMascotas,
                                        infoMascotas = infoOtrasMascotas,
                                        compromiso = compromisoChecked,
                                        firma = firmaNombre,
                                        tiempoSola = tiempoSola,
                                        responsable = responsable,
                                        presupuestoOk = presupuestoOk,
                                        estado = "pendiente"
                                    )
                                    val res = repo.crearSolicitud(solicitud)
                                    enviando = false
                                    if (res.isSuccess) {
                                        onFinalizar()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8D4934),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth(0.6f).height(54.dp),
                        enabled = !enviando && when(paso) {
                            1 -> nombreCompleto.isNotBlank() && direccion.isNotBlank() && tipoVivienda.isNotBlank()
                            2 -> tiempoSola.isNotBlank() && responsable.isNotBlank()
                            3 -> compromisoChecked && firmaNombre.isNotBlank()
                            else -> true
                        }
                    ) {
                        Text(if(paso == 3) "Enviar Solicitud" else "Siguiente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        if (enviando) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(if(paso == 3) Icons.Default.CheckCircle else Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            
            if (paso == 1) {
                item {
                    Text("Solicitud de Adopción", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = GrisTexto)
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4E9)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF5A6632), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Esta información nos ayuda a asegurar que el hogar de ${perro.nombre} sea el más adecuado para su personalidad y necesidades específicas. ¡Gracias por abrir tu corazón!",
                                fontSize = 13.sp,
                                color = Color(0xFF5A6632)
                            )
                        }
                    }
                }
                item {
                    Text("Nombre Completo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    OutlinedTextField(
                        value = nombreCompleto,
                        onValueChange = { nombreCompleto = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Mariana González", color = Color.LightGray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                    )
                }
                item {
                    Text("Dirección de Residencia", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Calle, número, colonia y ciudad", color = Color.LightGray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                    )
                }
                
                item {
                    Text("Tipo de Vivienda", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = tipoVivienda,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            placeholder = { Text("Seleccionar..", color = Color.LightGray) },
                            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.8f)) {
                            listOf("Casa con jardín", "Departamento", "Casa de campo", "Otro").forEach { tipo ->
                                DropdownMenuItem(text = { Text(tipo) }, onClick = { tipoVivienda = tipo; expanded = false })
                            }
                        }
                    }
                }

                item {
                    Text("¿Tienes otras mascotas?", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F7))
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                                .background(if (tieneOtrasMascotas) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { tieneOtrasMascotas = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sí", fontWeight = FontWeight.Bold, color = if (tieneOtrasMascotas) Color(0xFF8D4934) else GrisSecundario)
                        }
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                                .background(if (!tieneOtrasMascotas) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { tieneOtrasMascotas = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No", fontWeight = FontWeight.Bold, color = if (!tieneOtrasMascotas) Color(0xFF8D4934) else GrisSecundario)
                        }
                    }
                }

                item {
                    Text("Cuéntanos sobre tus otras mascotas (opcional)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    OutlinedTextField(
                        value = infoOtrasMascotas,
                        onValueChange = { infoOtrasMascotas = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Especies, edades y temperamento...", color = Color.LightGray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                    )
                }
                
                item {
                    CardMascotaResumen(perro)
                }
                item { Spacer(Modifier.height(20.dp)) }
            } else if (paso == 2) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = perro.fotoUri,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Estilo de Vida", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
                            Text("Cuestionario de compatibilidad para ${perro.nombre}", fontSize = 13.sp, color = GrisSecundario)
                        }
                    }
                }
                
                item {
                    Text("¿Cuánto tiempo pasará la mascota sola al día?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GrisTexto)
                }
                
                val opcionesTiempo = listOf(
                    "Menos de 4h" to Icons.Default.Schedule,
                    "4-8h" to Icons.Default.WorkOutline,
                    "Más de 8h" to Icons.Default.NightsStay
                )
                
                opcionesTiempo.forEach { (texto, icono) ->
                    item {
                        val seleccionada = tiempoSola == texto
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { tiempoSola = texto },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (seleccionada) Color(0xFFFDE7E1) else Color.White),
                            border = BorderStroke(1.dp, if (seleccionada) Color(0xFFE67E5D) else Color(0xFFF0F0F7))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icono, null, tint = if (seleccionada) Color(0xFF8D4934) else GrisSecundario, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Text(texto, fontWeight = FontWeight.Medium, color = GrisTexto)
                                }
                                RadioButton(
                                    selected = seleccionada,
                                    onClick = { tiempoSola = texto },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE67E5D))
                                )
                            }
                        }
                    }
                }
                
                item {
                    Text("¿Quién será el responsable principal del cuidado?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GrisTexto)
                    OutlinedTextField(
                        value = responsable,
                        onValueChange = { responsable = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nombre completo del cuidador", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.PersonOutline, null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8F9FF), focusedContainerColor = Color.White)
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Payments, null, tint = Color(0xFF0288D1))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Presupuesto Mensual", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("¿Cuentas con un presupuesto para gastos veterinarios y alimentación?", fontSize = 11.sp, color = GrisSecundario)
                                }
                            }
                            Switch(
                                checked = presupuestoOk,
                                onCheckedChange = { presupuestoOk = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF5A6632))
                            )
                        }
                    }
                }
                
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEBE9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Info, null, tint = GrisSecundario, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Esta información nos ayuda a asegurar que el estilo de vida del adoptante coincida con el nivel de energía y atención que ${perro.nombre} requiere para florecer en su nuevo hogar.",
                                fontSize = 11.sp,
                                color = GrisSecundario
                            )
                        }
                    }
                }

            } else if (paso == 3) {
                item {
                    Text("Compromiso y Firma", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Text("Estás a un paso de cambiar la vida de ${perro.nombre} para siempre.", fontSize = 14.sp, color = GrisSecundario, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                
                item {
                    // Pet mini card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF0F0F7))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = perro.fotoUri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF8D4934))
                                Surface(color = Color(0xFFF1F4E9), shape = RoundedCornerShape(8.dp)) {
                                    Text("ESPERÁNDOTE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6632))
                                }
                            }
                        }
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFDE7E1))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = compromisoChecked, 
                                onCheckedChange = { compromisoChecked = it }, 
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8D4934))
                            )
                            Text(
                                buildAnnotatedString {
                                    append("\"Me comprometo a brindar amor, cuidados médicos y protección a ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))) {
                                        append(perro.nombre)
                                    }
                                    append(" durante toda su vida.\"")
                                },
                                fontSize = 14.sp,
                                color = GrisTexto
                            )
                        }
                    }
                }
                
                item {
                    Text("Firma Digital o Nombre Completo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
                    val density = LocalDensity.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(16.dp))
                            .padding(2.dp)
                    ) {
                        // Using a dashed border look for the signature area
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                            drawRoundRect(color = Color.LightGray.copy(alpha = 0.5f), style = stroke, cornerRadius = CornerRadius(with(density) { 16.dp.toPx() }))
                        }
                        
                        OutlinedTextField(
                            value = firmaNombre,
                            onValueChange = { firmaNombre = it },
                            modifier = Modifier.fillMaxSize(),
                            placeholder = { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.Edit, null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Firma aquí o escribe tu nombre para validar", color = Color.LightGray, fontSize = 13.sp, textAlign = TextAlign.Center)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                        )
                    }
                }
                
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F8)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF4A5568), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Al enviar esta solicitud, el albergue revisará tu perfil y te contactará en un plazo de 48-72 horas.",
                                fontSize = 12.sp,
                                color = Color(0xFF4A5568)
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(30.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════
//  MIS SOLICITUDES (VISTA PARA EL ADOPTANTE)
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onVerDetallePerro: (String) -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    var solicitudes by remember { mutableStateOf<List<SolicitudAdopcion>>(emptyList()) }
    var perrosMap by remember { mutableStateOf<Map<String, PerroAdopcion>>(emptyMap()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val lista = repo.obtenerSolicitudesDeUsuario(usuario.id)
        solicitudes = lista
        
        // Cargar info de los perros para mostrar fotos/nombres
        val mapas = mutableMapOf<String, PerroAdopcion>()
        lista.forEach { sol ->
            repo.obtenerPerroPorId(sol.perroId)?.let { mapas[sol.perroId] = it }
        }
        perrosMap = mapas
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Postulaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8D4934))
            }
        } else if (solicitudes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.History, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                Spacer(Modifier.height(16.dp))
                Text("Aún no has enviado solicitudes", color = GrisSecundario, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(solicitudes) { sol ->
                    val perro = perrosMap[sol.perroId]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = perro?.fotoUri,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(perro?.nombre ?: "Cargando...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Enviada el ${sol.fecha}", fontSize = 12.sp, color = GrisSecundario)
                                Spacer(Modifier.height(4.dp))
                                ChipEstadoSolicitud(sol.estado)
                            }
                            IconButton(onClick = { onVerDetallePerro(sol.perroId) }) {
                                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF8D4934))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChipEstadoSolicitud(estado: String) {
    val (color, texto) = when(estado) {
        "aprobada" -> Color(0xFF4CAF50) to "¡APROBADA!"
        "rechazada" -> Color(0xFFF44336) to "No seleccionado"
        else -> Color(0xFFFF9800) to "En revisión"
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun CampoTextoForm(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, singleLine: Boolean = true, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GrisTexto)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.LightGray) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun CardMascotaResumen(perro: PerroAdopcion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = perro.fotoUri,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${perro.edad} • ${perro.tamano}", color = GrisSecundario, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E5D), modifier = Modifier.size(14.dp))
                    Text(perro.zona, color = GrisSecundario, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Vacunado", "Sociable").forEach { tag ->
                        Surface(color = Color(0xFFF1F4E9), shape = RoundedCornerShape(4.dp)) {
                            Text(tag, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF5A6632))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarPerroScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    perroAEditar: PerroAdopcion? = null,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(perroAEditar?.nombre ?: "") }
    var especie by remember { mutableStateOf(perroAEditar?.especie ?: "Perro") }
    var raza by remember { mutableStateOf(perroAEditar?.raza ?: "") }
    var sexo by remember { mutableStateOf(perroAEditar?.sexo ?: "Macho") }
    var energia by remember { mutableStateOf(perroAEditar?.energia ?: "Media") }
    var tamano by remember { mutableStateOf(perroAEditar?.tamano ?: "Mediano") }
    var edad by remember { mutableStateOf(perroAEditar?.edad ?: "") }
    var color by remember { mutableStateOf(perroAEditar?.color ?: "") }
    var zona by remember { mutableStateOf(perroAEditar?.zona ?: "") }
    var descripcion by remember { mutableStateOf(perroAEditar?.descripcion ?: "") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var fotoUrlExistente by remember { mutableStateOf(perroAEditar?.fotoUri ?: "") }

    var subiendo by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var exito by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imagenUri = it
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (perroAEditar != null) "Editar Huellita" else "Publicar Huellita", fontWeight = FontWeight.Bold) },
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
                    } else if (fotoUrlExistente.isNotBlank()) {
                        AsyncImage(
                            model = fotoUrlExistente,
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Sexo", fontWeight = FontWeight.Bold, color = GrisTexto, fontSize = 14.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Macho", "Hembra").forEach { s ->
                                FilterChip(
                                    selected = sexo == s,
                                    onClick = { sexo = s },
                                    label = { Text(s, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Energía", fontWeight = FontWeight.Bold, color = GrisTexto, fontSize = 14.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Baja", "Media", "Alta").forEach { e ->
                                FilterChip(
                                    selected = energia == e,
                                    onClick = { energia = e },
                                    label = { Text(e, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
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
                                mensaje = if (perroAEditar != null) "Actualizando..." else "Publicando..."
                                
                                val fotoUrl = if (imagenUri != null) {
                                    repo.subirImagen(imagenUri!!, "pets") ?: fotoUrlExistente.ifBlank { "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500" }
                                } else {
                                    fotoUrlExistente.ifBlank { "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500" }
                                }

                                val perroData = (perroAEditar ?: PerroAdopcion()).copy(
                                    nombre = nombre,
                                    especie = especie,
                                    raza = raza,
                                    sexo = sexo,
                                    energia = energia,
                                    tamano = tamano,
                                    edad = edad,
                                    color = color,
                                    zona = zona,
                                    descripcion = descripcion,
                                    fotoUri = fotoUrl,
                                    albergueId = usuario.id,
                                    albergueNombre = usuario.nombre
                                )
                                
                                val ok = if (perroAEditar != null) {
                                    repo.actualizarPerfilMascota(perroData)
                                } else {
                                    repo.agregarPerro(perroData).isNotBlank()
                                }

                                subiendo = false
                                if (ok) {
                                    exito = true
                                    mensaje = if (perroAEditar != null) "¡Cambios guardados!" else "¡Huellita publicada con éxito!"
                                } else {
                                    exito = false
                                    mensaje = "Error al procesar la solicitud"
                                }
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
                        Text(if (perroAEditar != null) "Guardar Cambios" else "Publicar para Adopción", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

// ══════════════════════════════════════════════════════════
//  NUEVAS PANTALLAS: ÉXITO Y HISTORIAS
// ══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudExitoScreen(
    perro: PerroAdopcion,
    onVolverInicio: () -> Unit,
    onVerHistorias: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Scaffold(
        containerColor = Color(0xFFF9FAFF),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, null, tint = Color(0xFF8D4934), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("HuellasHogar", fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))
                    }
                },
                actions = {
                    IconButton(onClick = onVolverInicio) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(20.dp))
                // Corazón con movimiento
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = Color(0xFFD7E5A4).copy(alpha = 0.5f)
                    ) {}
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale),
                        tint = Color(0xFF5A6632)
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                Text(
                    "¡Solicitud Enviada con Éxito!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF8D4934),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "${perro.nombre} está un paso más cerca de conocerte. Tu interés significa el mundo para nosotros.",
                    fontSize = 15.sp,
                    color = GrisSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            item {
                Spacer(Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F7))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFFD7E5A4), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("¿Qué sucede ahora?", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    buildAnnotatedString {
                                        append("El ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Refugio Esperanza") }
                                        append(" revisará tu perfil y te contactará en ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF8D4934))) { append("48-72 horas") }
                                        append(" para coordinar una entrevista inicial.")
                                    },
                                    fontSize = 13.sp, color = GrisSecundario
                                )
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Email, null, tint = Color(0xFFD7E5A4), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Mantente atento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Te hemos enviado una copia de tu solicitud y el manual de adopción responsable a tu correo electrónico.", fontSize = 13.sp, color = GrisSecundario)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onVolverInicio,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4934)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Volver al Inicio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            item {
                Spacer(Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Finales Felices", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onVerHistorias) {
                        Text("Ver más historias", color = Color(0xFF8D4934), fontSize = 14.sp)
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp), tint = Color(0xFF8D4934))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Muestra un par de "Finales Felices" de ejemplo (estáticos o mock)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=200",
                            contentDescription = null,
                            modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ADOPTADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6632))
                                Text(" • Hace 2 meses", fontSize = 10.sp, color = GrisSecundario)
                            }
                            Text("Milo y Sofía", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("\"Milo cambió mis...\"", fontSize = 12.sp, color = GrisSecundario)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?q=80&w=200",
                            contentDescription = null,
                            modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ADOPTADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6632))
                                Text(" • Hace 1 mes", fontSize = 10.sp, color = GrisSecundario)
                            }
                            Text("Oliver y Don Raúl", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Una compañía...", fontSize = 12.sp, color = GrisSecundario)
                        }
                    }
                }
            }
            
            item {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4E9))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = BlancoPuro) {
                            Icon(Icons.Default.Groups, null, modifier = Modifier.padding(8.dp), tint = Color(0xFF5A6632))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Únete a la comunidad", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5A6632))
                            Text("Comparte tu espera con otros adoptantes en nuestro grupo oficial.", fontSize = 11.sp, color = Color(0xFF5A6632).copy(alpha = 0.8f))
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoriasAdopcionScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finales Felices", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Outlined.NotificationsNone, null) } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Inspirate con las historias de amor de nuestra comunidad.", color = GrisSecundario, fontSize = 14.sp)
            }
            
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = {}, label = { Text("Todos") }, shape = RoundedCornerShape(20.dp), colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8D4934), selectedLabelColor = Color.White))
                    FilterChip(selected = false, onClick = {}, label = { Text("Perros") }, shape = RoundedCornerShape(20.dp), leadingIcon = { Icon(Icons.Default.Pets, null, modifier = Modifier.size(16.dp))})
                    FilterChip(selected = false, onClick = {}, label = { Text("Gatos") }, shape = RoundedCornerShape(20.dp), leadingIcon = { Icon(Icons.Default.Pets, null, modifier = Modifier.size(16.dp))})
                }
            }

            items(listOf(
                HistoriaData("La nueva vida de Luna", "Luna pasó 3 años esperando en el refugio. Hoy corre libre en un jardín inmenso...", "Familia Martinez", "https://images.unsplash.com/photo-1534361960057-19889db9621e?q=80&w=400", "Hace 2 días"),
                HistoriaData("Oliver encontró paz", "Oliver era un gato tímido que ahora es el mejor compañero de lectura de Doñ...", "Don Raúl", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400", "Hace 1 semana"),
                HistoriaData("Max y su mejor amigo", "Desde que Max llegó a casa, Lucas no ha dejado de sonreír. Son inseparables...", "Familia Lopez", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=400", "Hace 3 días")
            )) { historia ->
                CardHistoria(historia)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE67E5D))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("¿Tienes una historia?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text("Nos encantaría conocer cómo cambió tu vida al adoptar. Comparte tu final feliz con la comunidad.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, tint = Color(0xFFE67E5D), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Escribir mi historia", color = Color(0xFFE67E5D), fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(24.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, null, modifier = Modifier.size(48.dp), tint = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CardHistoria(historia: HistoriaData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(Modifier.height(200.dp).fillMaxWidth()) {
                AsyncImage(model = historia.foto, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Surface(
                    color = Color(0xFFE67E5D).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                ) {
                    Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Historia Destacada", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(historia.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(historia.fecha, fontSize = 11.sp, color = GrisSecundario)
                }
                Spacer(Modifier.height(8.dp))
                Text(historia.descripcion, fontSize = 13.sp, color = GrisSecundario)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFD7E5A4)), contentAlignment = Alignment.Center) {
                        Text(historia.autor.first().toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5A6632))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(historia.autor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = {}) {
                        Text("Leer más", color = Color(0xFF8D4934), fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp), tint = Color(0xFF8D4934))
                    }
                }
            }
        }
    }
}

data class HistoriaData(
    val titulo: String,
    val descripcion: String,
    val autor: String,
    val foto: String,
    val fecha: String
)
