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
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    usuario: Usuario,
    onIrAPerfil: () -> Unit,
    onIrAAdopciones: () -> Unit,
    onIrAMapa: () -> Unit,
    onIrAPerrosPerdidos: () -> Unit,
    onIrAMatch: () -> Unit,
    onIrADonaciones: () -> Unit,
    onIrAApoyo: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val repo = remember { HuellitasRepository() }
    var todosLosPerros by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var perrosFiltrados by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        todosLosPerros = repo.obtenerPerrosDisponibles()
        perrosFiltrados = todosLosPerros.take(8)
    }

    // Lógica de filtrado
    LaunchedEffect(filtroSeleccionado, todosLosPerros) {
        perrosFiltrados = when (filtroSeleccionado) {
            "Todos" -> todosLosPerros
            "Perros" -> todosLosPerros.filter { !it.raza.contains("Gato", true) }
            "Gatos" -> todosLosPerros.filter { it.raza.contains("Gato", true) }
            "Cachorros" -> todosLosPerros.filter { it.edad.contains("Meses", true) || it.edad.contains("Cachorro", true) }
            "Energía Baja" -> todosLosPerros.filter { it.descripcion.contains("tranquilo", true) || it.descripcion.contains("calma", true) }
            else -> todosLosPerros
        }.take(8)
    }

    Scaffold(
        containerColor = BlancoPuro,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            tint = NaranjaHuellitas,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Huellitas",
                            fontWeight = FontWeight.Bold,
                            color = MarronSuave,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, null, tint = MarronSuave)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoPuro)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de Bienvenida
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Bienvenido,",
                        fontSize = 16.sp,
                        color = GrisSecundario
                    )
                    Text(
                        "Hola, ${usuario.nombre.split(" ").first()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GrisSecundario.copy(alpha = 0.2f))
                        .clickable { onIrAPerfil() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MarronSuave
                    )
                }
            }

            // Grid de Acciones
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TarjetaMenu(
                        titulo = "Adoptar",
                        subtitulo = "Encuentra a tu nuevo mejor amigo en..",
                        icono = Icons.Default.Search,
                        colorIcono = Color(0xFF8D4934),
                        colorIconoBg = Color(0xFFFDE7E1),
                        mostrarHuella = true,
                        modifier = Modifier.weight(1f),
                        onClick = onIrAAdopciones
                    )
                    TarjetaMenu(
                        titulo = "Match\nInteligente",
                        subtitulo = "Te recomendamos la mascota ideal para ti...",
                        icono = Icons.Default.Favorite,
                        colorFondo = Color(0xFFE67E5D),
                        colorTexto = BlancoPuro,
                        colorSubtexto = BlancoPuro.copy(alpha = 0.9f),
                        colorIcono = BlancoPuro,
                        colorIconoBg = BlancoPuro.copy(alpha = 0.2f),
                        modifier = Modifier.weight(1f),
                        onClick = onIrAMatch
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TarjetaMenu(
                        titulo = "Donar\nArtículos",
                        subtitulo = "Economía circular para ayudar a los refugios.",
                        icono = Icons.Default.Eco,
                        colorFondo = ColorDonarBg,
                        colorTexto = ColorDonarTexto,
                        colorSubtexto = ColorDonarTexto.copy(alpha = 0.7f),
                        colorIcono = ColorDonarTexto,
                        colorIconoBg = Color(0xFFC5D68A),
                        modifier = Modifier.weight(1f),
                        onClick = onIrADonaciones
                    )
                    TarjetaMenu(
                        titulo = "Apoyo\nEconómico",
                        subtitulo = "Contribuye al bienestar de los animales...",
                        icono = Icons.Default.VolunteerActivism,
                        colorIcono = Color(0xFF455A64),
                        colorIconoBg = ColorIconoApoyo,
                        modifier = Modifier.weight(1f),
                        onClick = onIrAApoyo
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

    // Sección Destacados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Destacados para ti",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
                TextButton(onClick = onIrAAdopciones) {
                    Text("Ver todos", color = MarronSuave)
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(perrosFiltrados) { perro ->
                    TarjetaPerroDestacado(perro) { onIrAAdopciones() }
                }
            }
            
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun TarjetaMenu(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    colorFondo: Color = BlancoPuro,
    colorTexto: Color = GrisTexto,
    colorSubtexto: Color = GrisSecundario,
    colorIcono: Color = MarronSuave,
    colorIconoBg: Color = colorIcono.copy(alpha = 0.1f),
    mostrarHuella: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(190.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(if (colorFondo == BlancoPuro) 2.dp else 0.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Huella de agua (watermark) solo si se activa
            if (mostrarHuella) {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp),
                    tint = GrisSecundario.copy(alpha = 0.05f)
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colorIconoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    titulo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = colorTexto,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    subtitulo,
                    fontSize = 12.sp,
                    color = colorSubtexto,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TarjetaPerroDestacado(perro: PerroAdopcion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                if (perro.fotoUri.isNotEmpty()) {
                    AsyncImage(
                        model = perro.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(NaranjaClaro))
                }
                
                // Badge de "Vacunada" o similar
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCEDC8))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF558B2F), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Vacunada", fontSize = 10.sp, color = Color(0xFF33691E))
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
                Text(perro.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${perro.raza} • ${perro.edad}", fontSize = 13.sp, color = GrisSecundario)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = NaranjaHuellitas, modifier = Modifier.size(14.dp))
                    Text(perro.zona, fontSize = 12.sp, color = GrisSecundario)
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

