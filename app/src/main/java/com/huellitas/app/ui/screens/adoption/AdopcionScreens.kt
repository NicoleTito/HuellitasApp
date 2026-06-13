package com.huellitas.app.ui.screens.adoption

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    onNavigateToImpacto: () -> Unit = {}
) {
    val repo = remember { HuellitasRepository() }
    var perros by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var searchTexto by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        perros = repo.obtenerPerrosDisponibles()
    }

    Scaffold(
        containerColor = BlancoPuro,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Pets, 
                            null, 
                            tint = Color(0xFF8D4934), 
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "HuellasHogar", 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color(0xFF8D4934), 
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.LocationOn, 
                            null, 
                            tint = GrisSecundario, 
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "CDMX", 
                            fontSize = 12.sp, 
                            color = GrisSecundario,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.NotificationsNone, null, tint = GrisTexto)
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Buscador
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

            // Filtros rápidos
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text("Filtros", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Tune, null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE67E5D),
                                selectedLabelColor = BlancoPuro,
                                selectedLeadingIconColor = BlancoPuro
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(listOf("Perros", "Gatos", "Energía", "Tamaño")) { tag ->
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text(tag, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFF0F0F2),
                                labelColor = GrisTexto
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Grid de mascotas
            items(perros) { perro ->
                TarjetaMascotaGrid(perro) { onVerDetalle(perro) }
            }

            // Banner promocional
            item(span = { GridItemSpan(2) }) {
                BannerHistorias()
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TarjetaMascotaGrid(perro: PerroAdopcion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        Text(if (perro.raza.contains("Gato", true)) "🐱" else "🐶", fontSize = 40.sp)
                    }
                }
                
                // Botón Like
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(BlancoPuro.copy(alpha = 0.8f)),
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
                    Text(
                        perro.nombre, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 18.sp,
                        color = GrisTexto
                    )
                    
                    // Badge de edad
                    Surface(
                        color = Color(0xFFFDE7E1),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            perro.edad, 
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D4934)
                        )
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E5D), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("A 2.5 km", fontSize = 12.sp, color = GrisSecundario, fontWeight = FontWeight.Medium)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7E5A4)) // ColorDonarBg
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            // Watermark de hoja
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp),
                tint = Color(0xFF5A6632).copy(alpha = 0.1f) // ColorDonarTexto alpha
            )

            Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                Text(
                    "Adopta, no compres",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF5A6632) // ColorDonarTexto
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Dale una segunda oportunidad a un amigo que te espera.",
                    fontSize = 14.sp,
                    color = Color(0xFF5A6632).copy(alpha = 0.8f)
                )
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

// ══════════════════════════════════════════════════════════
//  DETALLE + SOLICITUD DE ADOPCIÓN (CONSERVADO)
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAdopcionScreen(
    perro: PerroAdopcion,
    usuarioId: String,
    onVolver: () -> Unit
) {
    val repo      = remember { HuellitasRepository() }
    val scope     = rememberCoroutineScope()
    var mensaje   by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var isError   by remember { mutableStateOf(false) }
    var enviando  by remember { mutableStateOf(false) }
    var enviado   by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(perro.nombre, fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NaranjaClaro),
                    contentAlignment = Alignment.Center
                ) {
                    if (perro.fotoUri.isNotBlank()) {
                        AsyncImage(
                            model = perro.fotoUri,
                            contentDescription = perro.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("🐶", fontSize = 72.sp)
                    }
                }
            }

            item {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    Text(perro.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                    ChipEstado(perro.estado)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Información", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                        Spacer(Modifier.height(10.dp))
                        FilaInfo("Raza",  perro.raza)
                        FilaInfo("Color", perro.color)
                        FilaInfo("Edad",  perro.edad)
                        FilaInfo("Zona",  perro.zona)
                        FilaInfo("Albergue", perro.albergueNombre)
                    }
                }
            }

            if (perro.descripcion.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoPuro)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Sobre ${perro.nombre}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                            Spacer(Modifier.height(8.dp))
                            Text(perro.descripcion, color = GrisSecundario, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }

            if (perro.estado == "disponible" && !enviado) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoPuro)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Solicitar adopción", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value         = mensaje,
                                onValueChange = { mensaje = it },
                                label         = { Text("¿Por qué quieres adoptar?") },
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                shape         = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            if (resultado.isNotBlank()) {
                                Text(resultado, color = if (isError) RojoError else VerdeExito, fontSize = 13.sp)
                                Spacer(Modifier.height(10.dp))
                            }

                            Button(
                                onClick  = {
                                    if (mensaje.isBlank()) {
                                        resultado = "Escribe un mensaje"
                                        isError   = true
                                    } else {
                                        scope.launch {
                                            enviando = true
                                            val sol = SolicitudAdopcion(
                                                perroId   = perro.id,
                                                usuarioId = usuarioId,
                                                mensaje   = mensaje
                                            )
                                            val res = repo.crearSolicitud(sol)
                                            enviando = false
                                            res.onSuccess {
                                                resultado = "¡Solicitud enviada!"
                                                isError   = false
                                                enviado   = true
                                            }
                                            res.onFailure {
                                                resultado = it.message ?: "Error"
                                                isError   = true
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !enviando,
                                colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas)
                            ) {
                                Text(if (enviando) "Enviando..." else "Enviar solicitud", color = BlancoPuro)
                            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            estado.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
