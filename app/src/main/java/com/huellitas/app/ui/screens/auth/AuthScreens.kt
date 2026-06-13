package com.huellitas.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.components.*
import com.huellitas.app.ui.theme.*

// ══════════════════════════════════════════════════════════
//  PANTALLA LOGIN
// ══════════════════════════════════════════════════════════
@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    onIrARegistro: () -> Unit
) {
    val repo    = remember { HuellitasRepository() }
    val scope   = rememberCoroutineScope()

    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var verPass     by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf("") }
    var cargando    by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            // Logo
            LogoHuellitas()

            Spacer(Modifier.height(8.dp))
            Text(
                "Conectando corazones\ncon patitas que esperan",
                fontSize   = 14.sp,
                color      = GrisSecundario,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Tarjeta de login
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = BlancoPuro),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "Iniciar sesión",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GrisTexto
                    )
                    Spacer(Modifier.height(20.dp))

                    CampoTexto(
                        valor         = email,
                        onValorChange = { email = it; errorMsg = "" },
                        etiqueta      = "Correo electrónico",
                        leadingIcon   = { Icon(Icons.Default.Email, null, tint = GrisSecundario) }
                    )
                    Spacer(Modifier.height(14.dp))

                    CampoTexto(
                        valor         = password,
                        onValorChange = { password = it; errorMsg = "" },
                        etiqueta      = "Contraseña",
                        esPassword    = !verPass,
                        leadingIcon   = { Icon(Icons.Default.Lock, null, tint = GrisSecundario) },
                        trailingIcon  = {
                            IconButton(onClick = { verPass = !verPass }) {
                                Icon(
                                    if (verPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null, tint = GrisSecundario
                                )
                            }
                        }
                    )

                    AnimatedVisibility(errorMsg.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = RojoError, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(errorMsg, color = RojoError, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    if (cargando) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NaranjaHuellitas)
                        }
                    } else {
                        BotonPrimario(
                            texto   = "Ingresar",
                            onClick = {
                                when {
                                    email.isBlank()    -> errorMsg = "Ingresa tu correo"
                                    password.isBlank() -> errorMsg = "Ingresa tu contraseña"
                                    else -> {
                                        scope.launch {
                                            cargando = true
                                            val usuario = repo.loginUsuario(email.trim(), password)
                                            cargando = false
                                            if (usuario != null) onLoginExitoso(usuario)
                                            else errorMsg = "Correo o contraseña incorrectos"
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? ", color = GrisSecundario)
                Text("Regístrate aquí", color = NaranjaHuellitas, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════
//  PANTALLA REGISTRO
// ══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: (Usuario) -> Unit,
    onVolver: () -> Unit
) {
    val repo    = remember { HuellitasRepository() }
    val scope   = rememberCoroutineScope()

    var nombre    by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var rolSelec  by remember { mutableStateOf("dueño") }
    var errorMsg  by remember { mutableStateOf("") }
    var dropOpen  by remember { mutableStateOf(false) }

    val roles = listOf("dueño", "voluntario", "albergue")
    val rolesLabel = mapOf("dueño" to "Dueño de mascota", "voluntario" to "Voluntario", "albergue" to "Albergue / Refugio")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .imePadding()
        ) {
            Spacer(Modifier.height(16.dp))

            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, "Volver", tint = GrisTexto)
                }
                Spacer(Modifier.weight(1f))
                LogoHuellitas()
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text("Crear cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
            Text("Únete a la comunidad Huellitas", fontSize = 14.sp, color = GrisSecundario)
            Spacer(Modifier.height(20.dp))

            CampoTexto(
                valor = nombre, onValorChange = { nombre = it; errorMsg = "" },
                etiqueta = "Nombre completo",
                leadingIcon = { Icon(Icons.Default.Person, null, tint = GrisSecundario) }
            )
            Spacer(Modifier.height(12.dp))

            CampoTexto(
                valor = email, onValorChange = { email = it; errorMsg = "" },
                etiqueta = "Correo electrónico",
                leadingIcon = { Icon(Icons.Default.Email, null, tint = GrisSecundario) }
            )
            Spacer(Modifier.height(12.dp))

            CampoTexto(
                valor = telefono, onValorChange = { telefono = it },
                etiqueta = "Teléfono (opcional)",
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = GrisSecundario) }
            )
            Spacer(Modifier.height(12.dp))

            // Selector de rol
            ExposedDropdownMenuBox(
                expanded         = dropOpen,
                onExpandedChange = { dropOpen = it }
            ) {
                OutlinedTextField(
                    value         = rolesLabel[rolSelec] ?: rolSelec,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Tipo de usuario") },
                    leadingIcon   = { Icon(Icons.Default.Group, null, tint = GrisSecundario) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(dropOpen) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaranjaHuellitas,
                        focusedLabelColor  = NaranjaHuellitas
                    )
                )
                ExposedDropdownMenu(
                    expanded       = dropOpen,
                    onDismissRequest = { dropOpen = false }
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text    = { Text(rolesLabel[rol] ?: rol) },
                            onClick = { rolSelec = rol; dropOpen = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            CampoTexto(
                valor = password, onValorChange = { password = it; errorMsg = "" },
                etiqueta = "Contraseña", esPassword = true,
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = GrisSecundario) }
            )
            Spacer(Modifier.height(12.dp))

            CampoTexto(
                valor = confirmar, onValorChange = { confirmar = it; errorMsg = "" },
                etiqueta = "Confirmar contraseña", esPassword = true,
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = GrisSecundario) }
            )

            AnimatedVisibility(errorMsg.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = RojoError, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(errorMsg, color = RojoError, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            BotonPrimario(
                texto   = "Crear cuenta",
                onClick = {
                    when {
                        nombre.isBlank()            -> errorMsg = "Ingresa tu nombre"
                        email.isBlank()             -> errorMsg = "Ingresa tu correo"
                        !email.contains("@")        -> errorMsg = "Correo no válido"
                        password.length < 4         -> errorMsg = "La contraseña debe tener al menos 4 caracteres"
                        password != confirmar       -> errorMsg = "Las contraseñas no coinciden"
                        else -> {
                            scope.launch {
                                val nuevo = Usuario(
                                    nombre   = nombre.trim(),
                                    email    = email.trim(),
                                    password = password,
                                    rol      = rolSelec,
                                    telefono = telefono.trim()
                                )
                                val result = repo.registrarUsuario(nuevo)
                                result.onSuccess { id ->
                                    val creado = repo.obtenerUsuarioPorId(id)
                                    if (creado != null) onRegistroExitoso(creado)
                                }
                                result.onFailure { errorMsg = it.message ?: "Error al registrar" }
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            BotonSecundario(texto = "Ya tengo cuenta", onClick = onVolver)

            Spacer(Modifier.height(80.dp))
        }
    }
}

