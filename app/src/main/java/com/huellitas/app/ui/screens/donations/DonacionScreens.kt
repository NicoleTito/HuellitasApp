package com.huellitas.app.ui.screens.donations

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.huellitas.app.data.model.ArticuloDonacion
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonarArticulosScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onIrAFormulario: () -> Unit,
    onNavigateToCatalogo: () -> Unit = {},
    onNavigateToMatch: () -> Unit = {},
    onNavigateToImpacto: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    var donaciones by remember { mutableStateOf<List<ArticuloDonacion>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    
    // Estado para el detalle
    var articuloSeleccionado by remember { mutableStateOf<ArticuloDonacion?>(null) }

    if (articuloSeleccionado != null) {
        DetalleDonacionScreen(
            articulo = articuloSeleccionado!!,
            onVolver = { articuloSeleccionado = null }
        )
        return
    }

    LaunchedEffect(filtroSeleccionado) {
        cargando = true
        donaciones = repo.obtenerDonaciones(if (filtroSeleccionado == "Todos") "Todo" else filtroSeleccionado)
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Pets,
                            null,
                            tint = Color(0xFF8D4934),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "HuellasHogar",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color(0xFF8D4934)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NotificationsNone, null, tint = Color(0xFF333333))
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
                containerColor = Color.White,
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
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMatch,
                    icon = { Icon(Icons.Default.FavoriteBorder, null) },
                    label = { Text("Match", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Eco, null) },
                    label = { Text("Circular", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE67E5D),
                        selectedTextColor = Color(0xFFE67E5D),
                        indicatorColor = Color(0xFFFDE7E1),
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
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
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onIrAFormulario,
                containerColor = Color(0xFFE67E5D),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Buscar Artículos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFF1D1B20)
            )

            Spacer(Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("¿Qué necesita tu mascota?", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFFE67E5D),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // Filtros de Categoría
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categorias = listOf("Todos", "Camas", "Correas", "Alimento", "Juguetes")
                items(categorias) { cat ->
                    val selected = filtroSeleccionado == cat
                    FilterChip(
                        selected = selected,
                        onClick = { filtroSeleccionado = cat },
                        label = { 
                            Text(
                                cat, 
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF626F47),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFEEEEEE),
                            labelColor = Color(0xFF666666)
                        ),
                        border = null,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Grid de Artículos
            val donacionesFiltradas = donaciones.filter {
                it.nombre.contains(searchText, ignoreCase = true) ||
                it.descripcion.contains(searchText, ignoreCase = true)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (cargando) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false
                    ) {
                        items(6) { SkeletonArticulo() }
                    }
                } else if (donacionesFiltradas.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (searchText.isEmpty()) "No hay artículos disponibles." else "No se encontraron resultados.",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(donacionesFiltradas) { articulo ->
                            TarjetaArticuloGrid(articulo, onClick = { articuloSeleccionado = articulo })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaArticuloGrid(articulo: ArticuloDonacion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                AsyncImage(
                    model = if (articulo.fotoUri.isEmpty()) "https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?q=80&w=500" else articulo.fotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Badge de "Gratis"
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Eco, 
                            null, 
                            tint = Color(0xFF626F47), 
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Gratis", 
                            color = Color.Black, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(
                    articulo.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    articulo.descripcion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn, 
                        null, 
                        tint = Color.Gray, 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "A 2.5 km", // Mock distancia
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioDonacionScreen(usuario: com.huellitas.app.data.model.Usuario, onVolver: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    
    var nombre by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Correas") }
    var estadoSeleccionado by remember { mutableStateOf("Como Nuevo") }
    var descripcion by remember { mutableStateOf("") }
    var publicando by remember { mutableStateOf(false) }

    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, null, tint = NaranjaHuellitas, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("HuellasHogar", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MarronSuave)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MarronSuave)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, null, tint = MarronSuave)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Badge y Título
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("ECONOMÍA CIRCULAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Donar Accesorios", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF333333))
            Text(
                "Sube fotos de los artículos que tu mascota ya no necesita y ayuda a otros peludos en la comunidad.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            // Sección Fotos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fotos del artículo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("0/3", color = Color.Gray, fontSize = 12.sp)
                    }
                    Text("Sube hasta 3 fotos claras del artículo. La primera será la imagen de portada.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(85.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { launcher.launch("image/*") }
                                .padding(2.dp)
                        ) {
                            // Simulando borde punteado con fondo
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFDE7E1).copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
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
                                        Icon(Icons.Default.AddAPhoto, null, tint = Color(0xFF8D4934), modifier = Modifier.size(24.dp))
                                        Text("Añadir", fontSize = 11.sp, color = Color(0xFF8D4934), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .size(85.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F3F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, null, tint = Color.LightGray, modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Sección Detalles
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Detalles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Nombre del artículo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Ej. Cama ortopédica mediana") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedBorderColor = Color(0xFF8D4934)
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Categoría", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Camas", "Correas", "Alimento", "Juguetes").forEach { cat ->
                            val selected = categoriaSeleccionada == cat
                            FilterChip(
                                selected = selected,
                                onClick = { categoriaSeleccionada = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                shape = RoundedCornerShape(15.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDCEDC8),
                                    selectedLabelColor = Color(0xFF33691E),
                                    containerColor = Color.White,
                                    labelColor = Color.Gray
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selected) Color.Transparent else Color.LightGray,
                                    borderWidth = 1.dp,
                                    selected = selected,
                                    enabled = true
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Estado", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Nuevo", "Como Nuevo", "Usado").forEach { est ->
                            val selected = estadoSeleccionado == est
                            FilterChip(
                                selected = selected,
                                onClick = { estadoSeleccionado = est },
                                label = { Text(est, fontSize = 12.sp) },
                                shape = RoundedCornerShape(15.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDCEDC8),
                                    selectedLabelColor = Color(0xFF33691E),
                                    containerColor = Color.White,
                                    labelColor = Color.Gray
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selected) Color.Transparent else Color.LightGray,
                                    borderWidth = 1.dp,
                                    selected = selected,
                                    enabled = true
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Sección Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Añade detalles útiles: tamaño, marca o motivo de donación.", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = { Text("Describe el artículo aquí...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedBorderColor = Color(0xFF8D4934)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Punto de Entrega
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Punto de entrega", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Selecciona una ubicación aproximada para la recogida.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8EAF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF8D4934), modifier = Modifier.size(32.dp))
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Colonia Roma Sur", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF33691E)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDCEDC8)))
                    ) {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Usar mi ubicación actual", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (publicando) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8D4934))
                }
            } else {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && descripcion.isNotBlank()) {
                            scope.launch {
                                publicando = true
                                
                                // Intentamos subir
                                val subidaExitosas = if (imagenUri != null) {
                                    repo.subirImagen(imagenUri!!, "donations")
                                } else null

                                // Si la subida falló, asignamos una imagen premium según la categoría
                                val fotoFinal = subidaExitosas ?: when(categoriaSeleccionada) {
                                    "Alimento" -> "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?q=80&w=500"
                                    "Juguetes" -> "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?q=80&w=500"
                                    "Camas" -> "https://images.unsplash.com/photo-1541599540903-216a46ca1df0?q=80&w=500"
                                    else -> "https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?q=80&w=500"
                                }

                                val nuevaDonacion = ArticuloDonacion(
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    categoria = categoriaSeleccionada,
                                    donanteNombre = usuario.nombre,
                                    donanteId = usuario.id,
                                    fotoUri = fotoFinal
                                )
                                repo.agregarDonacion(nuevaDonacion)
                                publicando = false
                                onVolver()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E5D))
                ) {
                    Icon(Icons.Default.Favorite, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Publicar Donación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApoyoEconomicoScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apoyo Económico", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        },
        containerColor = CremaBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "💳",
                fontSize = 60.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "¡Próximamente!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Aquí podrás realizar donaciones monetarias directas a los refugios.",
                color = GrisSecundario,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleDonacionScreen(articulo: ArticuloDonacion, onVolver: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { HuellitasRepository(context) }
    var donante by remember { mutableStateOf<com.huellitas.app.data.model.Usuario?>(null) }

    LaunchedEffect(articulo.donanteId) {
        donante = repo.obtenerUsuarioPorId(articulo.donanteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Artículo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Donado por:", fontSize = 12.sp, color = Color.Gray)
                        Text(articulo.donanteNombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Button(
                        onClick = {
                            donante?.telefono?.let { tel ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://api.whatsapp.com/send?phone=$tel&text=Hola, estoy interesado en tu donación de ${articulo.nombre} en HuellitasApp")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Manejar error si no hay app de mensajería
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF626F47)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        enabled = donante != null && donante?.telefono?.isNotEmpty() == true
                    ) {
                        Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Contactar")
                    }
                }
            }
        }
    ) {
padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen con fallback de seguridad
            val imagenDetalle = if (articulo.fotoUri.isBlank()) {
                "https://images.unsplash.com/photo-1583512603805-3cc6b41f3edb?q=80&w=500"
            } else {
                articulo.fotoUri
            }

            AsyncImage(
                model = imagenDetalle,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(20.dp)) {
                // Categoría Badge
                Surface(
                    color = Color(0xFFFDE7E1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        articulo.categoria.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D4934)
                    )
                }

                Spacer(Modifier.height(12.dp))
                
                Text(
                    articulo.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Schedule, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Publicado el ${articulo.fechaPublicacion}", fontSize = 12.sp, color = Color.Gray)
                }

                HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    articulo.descripcion,
                    fontSize = 15.sp,
                    color = Color(0xFF444444),
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(24.dp))

                // Info de Entrega
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E5D))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Punto de entrega", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Colonia Roma Sur, CDMX", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
                
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun SkeletonArticulo() {
    val brush = rememberShimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(brush))
            Column(Modifier.padding(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).background(brush))
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).background(brush))
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.width(60.dp).height(12.dp).background(brush))
            }
        }
    }
}
