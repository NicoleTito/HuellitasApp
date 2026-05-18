package com.huellitas.app.ui.screens.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.components.*
import com.huellitas.app.ui.theme.*
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
//  LISTADO DE ADOPCIONES
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdopcionListScreen(
    usuarioId: Int,
    onVerDetalle: (PerroAdopcion) -> Unit
) {
    val context = LocalContext.current
    val repo    = remember { HuellitasRepository(context) }

    var perros      by remember { mutableStateOf(emptyList<PerroAdopcion>()) }
    var filtroRaza  by remember { mutableStateOf("") }
    var filtroZona  by remember { mutableStateOf("") }
    var mostrarFiltros by remember { mutableStateOf(false) }

    // Cargar perros al entrar
    LaunchedEffect(Unit) {
        perros = repo.obtenerPerrosDisponibles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Adopciones", fontWeight = FontWeight.Bold, color = GrisTexto)
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                        Icon(
                            Icons.Default.FilterList,
                            "Filtrar",
                            tint = if (mostrarFiltros) NaranjaHuellitas else GrisSecundario
                        )
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
                .padding(horizontal = 16.dp)
        ) {
            // Panel de filtros
            if (mostrarFiltros) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = BlancoPuro),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Filtrar por", fontWeight = FontWeight.SemiBold, color = GrisTexto)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value         = filtroRaza,
                                onValueChange = { filtroRaza = it },
                                label         = { Text("Raza") },
                                modifier      = Modifier.weight(1f),
                                shape         = RoundedCornerShape(10.dp),
                                singleLine    = true,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NaranjaHuellitas,
                                    focusedLabelColor  = NaranjaHuellitas
                                )
                            )
                            OutlinedTextField(
                                value         = filtroZona,
                                onValueChange = { filtroZona = it },
                                label         = { Text("Zona") },
                                modifier      = Modifier.weight(1f),
                                shape         = RoundedCornerShape(10.dp),
                                singleLine    = true,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NaranjaHuellitas,
                                    focusedLabelColor  = NaranjaHuellitas
                                )
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    perros = repo.obtenerPerrosPorFiltro(filtroRaza, filtroZona)
                                },
                                modifier = Modifier.weight(1f),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas)
                            ) { Text("Buscar") }
                            OutlinedButton(
                                onClick = {
                                    filtroRaza = ""; filtroZona = ""
                                    perros = repo.obtenerPerrosDisponibles()
                                },
                                modifier = Modifier.weight(1f),
                                shape    = RoundedCornerShape(10.dp)
                            ) { Text("Limpiar") }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Contador
            Text(
                "${perros.size} perritos esperando un hogar",
                fontSize = 13.sp,
                color    = GrisSecundario,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            if (perros.isEmpty()) {
                MensajeVacio("No hay perritos disponibles\npor ahora", "")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(perros) { perro ->
                        TarjetaPerro(perro = perro, onClick = { onVerDetalle(perro) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ─── Tarjeta individual de perro ─────────────────────────
@Composable
fun TarjetaPerro(perro: PerroAdopcion, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Foto placeholder (círculo con emoji)
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaranjaClaro),
                contentAlignment = Alignment.Center
            ) {
                Text("", fontSize = 32.sp)
                // Para foto real: AsyncImage(model = perro.fotoUri, ...)
                // requiere librería Coil: implementation("io.coil-kt:coil-compose:2.4.0")
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.SpaceBetween,
                    modifier               = Modifier.fillMaxWidth()
                ) {
                    Text(
                        perro.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 17.sp,
                        color      = GrisTexto
                    )
                    ChipEstado(perro.estado)
                }

                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (perro.raza.isNotBlank()) {
                        EtiquetaInfo("🐾 ${perro.raza}")
                    }
                    if (perro.edad.isNotBlank()) {
                        EtiquetaInfo(" ${perro.edad}")
                    }
                }

                Spacer(Modifier.height(4.dp))
                if (perro.zona.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = NaranjaHuellitas, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(perro.zona, fontSize = 12.sp, color = GrisSecundario)
                    }
                }

                if (perro.albergueNombre.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, null, tint = MarronSuave, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(perro.albergueNombre, fontSize = 12.sp, color = GrisSecundario)
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, null, tint = GrisSecundario)
        }
    }
}

@Composable
fun EtiquetaInfo(texto: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NaranjaClaro)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(texto, fontSize = 11.sp, color = MarronSuave, fontWeight = FontWeight.Medium)
    }
}

// ══════════════════════════════════════════════════════════
//  DETALLE + SOLICITUD DE ADOPCIÓN
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAdopcionScreen(
    perro: PerroAdopcion,
    usuarioId: Int,
    onVolver: () -> Unit
) {
    val context   = LocalContext.current
    val repo      = remember { HuellitasRepository(context) }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremaBg)
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
                // Foto grande
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NaranjaClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Text("", fontSize = 72.sp)
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
                TarjetaSeccion {
                    Text("Información", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                    Spacer(Modifier.height(10.dp))
                    FilaInfo("Raza",  perro.raza)
                    FilaInfo("Color", perro.color)
                    FilaInfo("Edad",  perro.edad)
                    FilaInfo("Zona",  perro.zona)
                    FilaInfo("Albergue", perro.albergueNombre)
                }
            }

            if (perro.descripcion.isNotBlank()) {
                item {
                    TarjetaSeccion {
                        Text("Sobre ${perro.nombre}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                        Spacer(Modifier.height(8.dp))
                        Text(perro.descripcion, color = GrisSecundario, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }

            if (perro.estado == "disponible" && !enviado) {
                item {
                    TarjetaSeccion {
                        Text("Solicitar adopción", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GrisTexto)
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value         = mensaje,
                            onValueChange = { mensaje = it },
                            label         = { Text("¿Por qué quieres adoptar a ${perro.nombre}?") },
                            modifier      = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape         = RoundedCornerShape(12.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NaranjaHuellitas,
                                focusedLabelColor  = NaranjaHuellitas
                            )
                        )
                        Spacer(Modifier.height(12.dp))

                        if (resultado.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isError) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    null,
                                    tint     = if (isError) RojoError else VerdeExito,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(resultado, color = if (isError) RojoError else VerdeExito, fontSize = 13.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                        }

                        BotonPrimario(
                            texto    = if (enviando) "Enviando..." else "Enviar solicitud ",
                            onClick  = {
                                if (mensaje.isBlank()) {
                                    resultado = "Escribe un mensaje para el albergue"
                                    isError   = true
                                } else {
                                    enviando = true
                                    val sol = SolicitudAdopcion(
                                        perroId   = perro.id,
                                        usuarioId = usuarioId,
                                        mensaje   = mensaje
                                    )
                                    val res = repo.crearSolicitud(sol)
                                    enviando = false
                                    res.onSuccess {
                                        resultado = "¡Solicitud enviada! El albergue se contactará contigo."
                                        isError   = false
                                        enviado   = true
                                    }
                                    res.onFailure {
                                        resultado = it.message ?: "Error al enviar solicitud"
                                        isError   = true
                                    }
                                }
                            },
                            habilitado = !enviando
                        )
                    }
                }
            }

            if (enviado) {
                item {
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("", fontSize = 36.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "¡Solicitud enviada!",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp,
                                color      = VerdeExito
                            )
                            Text(
                                "El albergue revisará tu solicitud y se pondrá en contacto contigo.",
                                fontSize  = 13.sp,
                                color     = GrisSecundario,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
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