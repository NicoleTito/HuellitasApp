package com.huellitas.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.components.*
import com.huellitas.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    onActualizarUsuario: (Usuario) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val repo    = remember { HuellitasRepository() }
    val scope   = rememberCoroutineScope()

    var modoEdicion  by remember { mutableStateOf(false) }
    var nombre       by remember { mutableStateOf(usuario.nombre) }
    var telefono     by remember { mutableStateOf(usuario.telefono) }
    var guardado     by remember { mutableStateOf(false) }
    var solicitudes  by remember { mutableStateOf(emptyList<SolicitudAdopcion>()) }
    var showDialog   by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        solicitudes = repo.obtenerSolicitudesDeUsuario(usuario.id)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon  = { Text("", fontSize = 28.sp) },
            title = { Text("Cerrar sesión") },
            text  = { Text("¿Estás seguro que deseas salir de tu cuenta?") },
            confirmButton = {
                Button(
                    onClick = { showDialog = false; onCerrarSesion() },
                    colors  = ButtonDefaults.buttonColors(containerColor = RojoError)
                ) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold, color = GrisTexto) },
                actions = {
                    if (!modoEdicion) {
                        IconButton(onClick = { modoEdicion = true; guardado = false }) {
                            Icon(Icons.Default.Edit, "Editar", tint = NaranjaHuellitas)
                        }
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Logout, "Salir", tint = GrisSecundario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremaBg)
            )
        },
        containerColor = CremaBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Avatar ────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(NaranjaHuellitas),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color      = BlancoPuro
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(usuario.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GrisTexto)

            // Badge de rol
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NaranjaClaro)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                val rolEmoji = when (usuario.rol) {
                    "albergue"   -> " Albergue"
                    "voluntario" -> " Voluntario"
                    else         -> " Dueño de mascota"
                }
                Text(rolEmoji, fontSize = 13.sp, color = NaranjaHuellitas, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))

            // ── Datos del perfil ──────────────────────────
            TarjetaSeccion {
                Text(
                    if (modoEdicion) "Editar datos" else "Mis datos",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = GrisTexto
                )
                Spacer(Modifier.height(14.dp))

                if (modoEdicion) {
                    CampoTexto(
                        valor         = nombre,
                        onValorChange = { nombre = it },
                        etiqueta      = "Nombre completo",
                        leadingIcon   = { Icon(Icons.Default.Person, null, tint = GrisSecundario) }
                    )
                    Spacer(Modifier.height(12.dp))
                    CampoTexto(
                        valor         = telefono,
                        onValorChange = { telefono = it },
                        etiqueta      = "Teléfono",
                        leadingIcon   = { Icon(Icons.Default.Phone, null, tint = GrisSecundario) }
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick  = { modoEdicion = false; nombre = usuario.nombre; telefono = usuario.telefono },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(12.dp)
                        ) { Text("Cancelar") }
                        Button(
                            onClick = {
                                scope.launch {
                                    val actualizado = usuario.copy(nombre = nombre.trim(), telefono = telefono.trim())
                                    val ok = repo.actualizarPerfil(actualizado)
                                    if (ok) {
                                        onActualizarUsuario(actualizado)
                                        modoEdicion = false
                                        guardado    = true
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas)
                        ) { Text("Guardar") }
                    }
                } else {
                    if (guardado) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = VerdeExito, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Perfil actualizado", color = VerdeExito, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    ItemPerfil(Icons.Default.Person, "Nombre", usuario.nombre)
                    ItemPerfil(Icons.Default.Email, "Correo", usuario.email)
                    if (usuario.telefono.isNotBlank())
                        ItemPerfil(Icons.Default.Phone, "Teléfono", usuario.telefono)
                    ItemPerfil(Icons.Default.CalendarMonth, "Miembro desde", usuario.fechaRegistro)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Mis solicitudes ───────────────────────────
            TarjetaSeccion {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Mis solicitudes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                    Box(
                        modifier         = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(NaranjaHuellitas),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${solicitudes.size}", color = BlancoPuro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (solicitudes.isEmpty()) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("", fontSize = 32.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Aún no has solicitado\nninguna adopción", color = GrisSecundario, fontSize = 13.sp)
                    }
                } else {
                    solicitudes.forEach { sol ->
                        TarjetaSolicitud(solicitud = sol)
                        if (sol != solicitudes.last()) {
                            Divider(color = NaranjaClaro, thickness = 1.dp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ItemPerfil(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NaranjaHuellitas, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 11.sp, color = GrisSecundario)
            Text(valor, fontSize = 14.sp, color = GrisTexto, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TarjetaSolicitud(solicitud: SolicitudAdopcion) {
    val (bg, fg, label) = when (solicitud.estado) {
        "aprobada"  -> Triple(androidx.compose.ui.graphics.Color(0xFFE8F5E9), VerdeExito, " Aprobada")
        "rechazada" -> Triple(androidx.compose.ui.graphics.Color(0xFFFFEBEE), RojoError, " Rechazada")
        else        -> Triple(androidx.compose.ui.graphics.Color(0xFFFFF8E1), AmarilloAlerta, " Pendiente")
    }
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NaranjaClaro),
            contentAlignment = Alignment.Center
        ) { Text("", fontSize = 20.sp) }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text("Solicitud #${solicitud.id}", fontWeight = FontWeight.Medium, color = GrisTexto, fontSize = 14.sp)
            Text(solicitud.fecha, fontSize = 12.sp, color = GrisSecundario)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(bg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

