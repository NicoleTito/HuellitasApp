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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import android.content.Intent
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(
    usuario: com.huellitas.app.data.model.Usuario,
    onVolver: () -> Unit,
    onNavigateToCatalogo: () -> Unit = {},
    onNavigateToPerdidos: () -> Unit = {},
    onNavigateToCircular: () -> Unit = {},
    onIrAPerfil: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { com.huellitas.app.data.repository.HuellitasRepository(context) }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    
    var donacionesRealizadas by remember { mutableIntStateOf(0) }
    var solicitudesAprobadas by remember { mutableIntStateOf(0) }
    var perrosPerdidosReportados by remember { mutableIntStateOf(0) }
    var mascotasPublicadas by remember { mutableIntStateOf(0) }

    var showBreakdown by remember { mutableStateOf(false) }
    var showPurposeInfo by remember { mutableStateOf(false) }

    val esAlbergue = usuario.rol == "albergue"
    val totalPuntos = if (esAlbergue) {
        (mascotasPublicadas * 50) + (solicitudesAprobadas * 300)
    } else {
        (donacionesRealizadas * 100) + (solicitudesAprobadas * 500) + (perrosPerdidosReportados * 200)
    }

    val cargarDatos = {
        scope.launch {
            isRefreshing = true
            if (esAlbergue) {
                mascotasPublicadas = repo.obtenerMisMascotas(usuario.id).size
                solicitudesAprobadas = repo.obtenerSolicitudesRecibidas(usuario.id).count { it.estado == "aprobada" }
            } else {
                donacionesRealizadas = repo.obtenerConteoDonacionesUsuario(usuario.id)
                solicitudesAprobadas = repo.obtenerConteoAdopcionesUsuario(usuario.id)
                perrosPerdidosReportados = repo.obtenerConteoPerrosPerdidosUsuario(usuario.id)
            }
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
                            .background(Color(0xFFFDE7E1))
                            .clickable { onIrAPerfil() },
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
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
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
                        unselectedIconColor = GrisSecundario,
                        unselectedTextColor = GrisSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCircular,
                    icon = { Icon(Icons.Default.Autorenew, null) },
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
                    selected = true,
                    onClick = { },
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(usuario.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                                IconButton(onClick = { showPurposeInfo = true }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(if (esAlbergue) "CENTRO DE ESPERANZA" else "GUARDIÁN DE LA TIERRA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4934).copy(alpha = 0.7f))
                            Spacer(Modifier.height(8.dp))
                            val factor = if (esAlbergue) (mascotasPublicadas + solicitudesAprobadas) else (donacionesRealizadas + solicitudesAprobadas + perrosPerdidosReportados)
                            val progreso = (factor % 2) * 0.5f
                            LinearProgressIndicator(
                                progress = { if (progreso == 0f && factor > 0) 1f else progreso.coerceAtLeast(0.1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = Color(0xFF626F47),
                                trackColor = Color(0xFFEEEEEE)
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Puntos Huella: $totalPuntos", fontSize = 10.sp, color = Color.Gray)
                                Text("Nivel: ${1 + factor/2}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
    
                Spacer(Modifier.height(24.dp))
                Text("Tus Medallas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (esAlbergue) {
                        MedallaItem("Hogar Activo", Icons.Default.Home, 
                            if (mascotasPublicadas > 5) Color(0xFFF1F8E9) else Color(0xFFF5F5F5), 
                            if (mascotasPublicadas > 5) Color(0xFF558B2F) else Color(0xFF616161), 
                            Modifier.weight(1f))
                        MedallaItem("Gran Puente", Icons.Default.Handshake, 
                            if (solicitudesAprobadas > 3) Color(0xFFFCE4EC) else Color(0xFFF5F5F5), 
                            if (solicitudesAprobadas > 3) Color(0xFF880E4F) else Color(0xFF616161), 
                            Modifier.weight(1f))
                        MedallaItem("Verificado", Icons.Default.Verified, 
                            Color(0xFFE8EAF6), Color(0xFF3F51B5), 
                            Modifier.weight(1f))
                    } else {
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
                            if (esAlbergue) {
                                ImpactoBox("${mascotasPublicadas}", "Huellitas publicadas", Icons.Default.Pets, Modifier.weight(1f))
                                ImpactoBox("${solicitudesAprobadas}", "Finales felices (Adopciones)", Icons.Default.Favorite, Modifier.weight(1f))
                            } else {
                                ImpactoBox("${solicitudesAprobadas}", "Mascotas con nuevo hogar", Icons.Default.Home, Modifier.weight(1f))
                                ImpactoBox("${donacionesRealizadas}", "Artículos re-circulados", Icons.Default.Autorenew, Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showBreakdown = true },
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
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "¡Únete a HuellasHogar y ayúdanos a dar segundas oportunidades a perritos que lo necesitan! Descarga la app aquí: https://huellitasapp.example.com")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(Icons.Default.ChevronRight, null)
                        }
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }

            // Modal Desglose
            if (showBreakdown) {
                ModalBottomSheet(
                    onDismissRequest = { showBreakdown = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text("Tu Impacto en Detalle", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF8D4934))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (esAlbergue) "Este es el resumen de la esperanza que has generado a través de tu gestión."
                            else "Aquí tienes el desglose de cómo has acumulado tus Puntos Huella hasta hoy.", 
                            fontSize = 14.sp, color = Color.Gray
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        if (esAlbergue) {
                            BreakdownRow("Huellitas Publicadas", mascotasPublicadas, 50, Icons.Default.Pets)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.1f))
                            BreakdownRow("Adopciones Concretadas", solicitudesAprobadas, 300, Icons.Default.ThumbUp)
                        } else {
                            BreakdownRow("Adopciones Aprobadas", solicitudesAprobadas, 500, Icons.Default.Pets)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.1f))
                            BreakdownRow("Donaciones Realizadas", donacionesRealizadas, 100, Icons.Default.VolunteerActivism)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.1f))
                            BreakdownRow("Reportes de Extravío", perrosPerdidosReportados, 200, Icons.Default.Search)
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFDE7E1))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Puntos Huella", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF8D4934))
                            Text("$totalPuntos", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF8D4934))
                        }
                        
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }

            // Dialog Propósito
            if (showPurposeInfo) {
                AlertDialog(
                    onDismissRequest = { showPurposeInfo = false },
                    confirmButton = {
                        TextButton(onClick = { showPurposeInfo = false }) {
                            Text("Entendido", color = Color(0xFF8D4934), fontWeight = FontWeight.Bold)
                        }
                    },
                    title = { Text(if (esAlbergue) "Propósito del Albergue" else "¿Por qué el Impacto?", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text(
                                if (esAlbergue) "Tu rol como albergue es el pilar de HuellasHogar. Tu impacto mide cuántas vidas has logrado conectar."
                                else "En HuellasHogar, cada acción que realizas tiene un valor real para nuestra comunidad animal.",
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (esAlbergue) "• El Nivel muestra tu trayectoria y confiabilidad.\n" +
                                               "• Publicar mascotas te otorga puntos de visibilidad.\n" +
                                               "• Las adopciones exitosas son tu mayor indicador de éxito."
                                else "• El Nivel refleja tu compromiso social.\n" +
                                     "• Los Puntos te permitirán pronto canjear beneficios con marcas aliadas.\n" +
                                     "• Tu actividad motiva a otros a participar en la economía circular y la tenencia responsable.",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White
                )
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
fun BreakdownRow(titulo: String, cantidad: Int, multiplicador: Int, icono: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("$cantidad x $multiplicador pts", fontSize = 12.sp, color = Color.Gray)
        }
        Text("+${cantidad * multiplicador}", fontWeight = FontWeight.Bold, color = Color(0xFF626F47))
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
