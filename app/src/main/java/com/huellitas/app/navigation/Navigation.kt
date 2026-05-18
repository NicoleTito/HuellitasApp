package com.huellitas.app.navigation

import androidx.compose.runtime.*
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.ui.screens.adoption.AdopcionListScreen
import com.huellitas.app.ui.screens.adoption.DetalleAdopcionScreen
import com.huellitas.app.ui.screens.auth.LoginScreen
import com.huellitas.app.ui.screens.auth.RegistroScreen
import com.huellitas.app.ui.screens.profile.PerfilScreen
import com.huellitas.app.ui.screens.main.MainScreen

// Rutas de navegación
sealed class Pantalla {
    object Login         : Pantalla()
    object Registro      : Pantalla()
    object Principal     : Pantalla()
    object Perfil        : Pantalla()
    object Adopciones    : Pantalla()
    object DetalleAdopcion : Pantalla()
}

@Composable
fun HuellitasNavHost() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Login) }
    var usuarioActual  by remember { mutableStateOf<Usuario?>(null) }
    var perroSeleccionado by remember { mutableStateOf<PerroAdopcion?>(null) }

    when (pantallaActual) {
        is Pantalla.Login -> {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    usuarioActual = usuario
                    pantallaActual = Pantalla.Principal
                },
                onIrARegistro = { pantallaActual = Pantalla.Registro }
            )
        }

        is Pantalla.Registro -> {
            RegistroScreen(
                onRegistroExitoso = { usuario ->
                    usuarioActual = usuario
                    pantallaActual = Pantalla.Principal
                },
                onVolver = { pantallaActual = Pantalla.Login }
            )
        }

        is Pantalla.Principal -> {
            MainScreen(
                usuario = usuarioActual!!,
                onIrAPerfil = { pantallaActual = Pantalla.Perfil },
                onIrAAdopciones = { pantallaActual = Pantalla.Adopciones },
                onCerrarSesion = {
                    usuarioActual = null
                    pantallaActual = Pantalla.Login
                }
            )
        }

        is Pantalla.Adopciones -> {
            AdopcionListScreen(
                usuarioId   = usuarioActual?.id ?: 0,
                onVerDetalle = { perro ->
                    perroSeleccionado = perro
                    pantallaActual = Pantalla.DetalleAdopcion
                }
            )
        }

        is Pantalla.DetalleAdopcion -> {
            perroSeleccionado?.let { perro ->
                DetalleAdopcionScreen(
                    perro     = perro,
                    usuarioId = usuarioActual?.id ?: 0,
                    onVolver  = { pantallaActual = Pantalla.Adopciones }
                )
            }
        }

        is Pantalla.Perfil -> {
            PerfilScreen(
                usuario            = usuarioActual!!,
                onActualizarUsuario = { actualizado -> usuarioActual = actualizado },
                onCerrarSesion     = {
                    usuarioActual = null
                    pantallaActual = Pantalla.Login
                }
            )
        }
    }
}

