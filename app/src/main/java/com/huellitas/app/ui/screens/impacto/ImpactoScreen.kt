package com.huellitas.app.ui.screens.impacto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onNavigateToCatalogo: () -> Unit = {},
    onNavigateToPerdidos: () -> Unit = {},
    onNavigateToCircular: () -> Unit = {}
) {
    val repo = remember { com.huellitas.app.data.repository.HuellitasRepository() }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    
    var donacionesRealizadas by remember { mutableIntStateOf(0) }
    var solicitudesAprobadas by remember { mutableIntStateOf(0) }
    var perrosPerdidosReportados by remember { mutableIntStateOf(0) }

    val cargarDatos = {
        scope.launch {
            isRefreshing = true
            donacionesRealizadas = repo.obtenerConteoDonacionesUsuario(usuario.id)
            solicitudesAprobadas = repo.obtenerConteoAdopcionesUsuario(usuario.id)
            perrosPerdidosReportados = repo.obtenerConteoPerrosPerdidosUsuario(usuario.id)
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, null, tint = Color(0xFF8D4934), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("HuellasHogar", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF8D4934))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NotificationsNone, null, tint = Color.Black)
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
                    onClick = onNavigateToPerdidos,
                    icon = { Icon(Icons.AutoMirrored.Filled.Announcement, null) },
                    label = { Text("Perdidos", fontSize = 10.sp) },
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
                    onClick = onNavigateToCircular,
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
                    selected = true,
                    onClick = { },
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
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(20.dp))
    
                // Perfil Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFDE7E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF8D4934)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .offset(x = 4.dp, y = 4.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8D4934))
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Lvl ${1 + (donacionesRealizadas + solicitudesAprobadas + perrosPerdidosReportados)/2}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(Modifier.width(16.dp))
                        
                        Column {
                            Text(usuario.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            Text("GUARDIÁN DE LA TIERRA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934).copy(alpha = 0.7f))
                            Spacer(Modifier.height(8.dp))
                            val progreso = ((donacionesRealizadas + solicitudesAprobadas + perrosPerdidosReportados) % 2) * 0.5f
                            LinearProgressIndicator(
                                progress = { if (progreso == 0f && (donacionesRealizadas + solicitudesAprobadas + perrosPerdidosReportados) > 0) 1f else progreso.coerceAtLeast(0.1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = Color(0xFF626F47),
                                trackColor = Color(0xFFEEEEEE)
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Puntos Huella: ${(donacionesRealizadas * 100) + (solicitudesAprobadas * 500) + (perrosPerdidosReportados * 200)}", fontSize = 10.sp, color = Color.Gray)
                                Text("Nivel: ${1 + (donacionesRealizadas + solicitudesAprobadas + perrosPerdidosReportados)/2}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
    
                Spacer(Modifier.height(24.dp))
                Text("Tus Medallas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MedallaItem("Donante Oro", Icons.Default.VolunteerActivism, 
                        if (donacionesRealizadas > 5) Color(0xFFF1F8E9) else Color(0xFFF5F5F5), 
                        if (donacionesRealizadas > 5) Color(0xFF558B2F) else Color(0xFF616161), 
                        Modifier.weight(1f))
                    MedallaItem("Eco Amigo", Icons.Default.Eco, 
                        if (donacionesRealizadas > 0) Color(0xFFFCE4EC) else Color(0xFFF5F5F5), 
                        if (donacionesRealizadas > 0) Color(0xFF880E4F) else Color(0xFF616161), 
                        Modifier.weight(1f))
                    MedallaItem("Adoptante", Icons.Default.Pets, 
                        if (solicitudesAprobadas > 0) Color(0xFFE8EAF6) else Color(0xFFF5F5F5), 
                        if (solicitudesAprobadas > 0) Color(0xFF3F51B5) else Color(0xFF616161), 
                        Modifier.weight(1f))
                    MedallaItem("Rescatista", Icons.Default.Handshake, 
                        if (perrosPerdidosReportados > 0) Color(0xFFFFF3E0) else Color(0xFFF5F5F5), 
                        if (perrosPerdidosReportados > 0) Color(0xFFE65100) else Color(0xFF616161), 
                        Modifier.weight(1f))
                }
    
                Spacer(Modifier.height(24.dp))
    
                // Calculadora Impacto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8D4934))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Calculadora de Impacto", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ImpactoBox("${solicitudesAprobadas}", "Mascotas con nuevo hogar", Icons.Default.Home, Modifier.weight(1f))
                            ImpactoBox("${donacionesRealizadas}", "Artículos donados", Icons.Default.Autorenew, Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ver desglose detallado", color = Color(0xFF8D4934), fontWeight = FontWeight.Bold)
                        }
                    }
                }
    
                Spacer(Modifier.height(24.dp))
                Text("Historial de Actividad", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
    
                Row(Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Tarjeta grande izquierda
                    Card(
                        modifier = Modifier.weight(1.2f).fillMaxHeight(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEDC8))
                    ) {
                        Column(Modifier.padding(16.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Icon(Icons.Default.History, null, tint = Color(0xFF33691E))
                                Spacer(Modifier.height(12.dp))
                                Text("Donación Mensual", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                                Text("Protectora Huellitas", fontSize = 12.sp, color = Color(0xFF33691E).copy(alpha = 0.7f))
                            }
                            Column {
                                Text("$0", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                                Text("Sin actividad reciente", fontSize = 10.sp, color = Color(0xFF33691E).copy(alpha = 0.7f))
                            }
                        }
                    }
    
                    // Columna de tarjetas pequeñas
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActividadPequena("Voluntariado", "Caminata Canina", Icons.Default.CalendarToday, Color(0xFFEEEEEE), Modifier.weight(1f))
                        ActividadPequena("Compra Eco", "Cama orgánica", Icons.Default.ShoppingCart, Color(0xFFFDE7E1), Modifier.weight(1f))
                    }
                }
    
                Spacer(Modifier.height(24.dp))
    
                // Invita Amigo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF626F47)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Invita a un amigo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Gana 50 puntos huella por cada amigo que se una.", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ChevronRight, null)
                        }
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }

            // Overlay de carga (Shimmer/Progress)
            if (isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8D4934))
                }
            }
        }
    }
}

@Composable
fun MedallaItem(nombre: String, icono: ImageVector, colorBg: Color, colorIcon: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, null, tint = colorIcon, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(nombre, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}

@Composable
fun ImpactoBox(valor: String, descripcion: String, icono: ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(16.dp)
    ) {
        Column {
            Icon(icono, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(valor, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(descripcion, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), lineHeight = 12.sp)
        }
    }
}

@Composable
fun ActividadPequena(titulo: String, subtitulo: String, icono: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitulo, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
