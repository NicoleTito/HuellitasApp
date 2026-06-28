package com.huellitas.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.ui.screens.adoption.AdopcionListScreen
import com.huellitas.app.ui.screens.adoption.DetalleAdopcionScreen
import com.huellitas.app.ui.screens.adoption.PublicarPerroScreen
import com.huellitas.app.ui.screens.auth.LoginScreen
import com.huellitas.app.ui.screens.auth.RegistroScreen
import com.huellitas.app.ui.screens.profile.PerfilScreen
import com.huellitas.app.ui.screens.main.MainScreen
import com.huellitas.app.ui.screens.maps.MapaAlberguesScreen
import com.huellitas.app.ui.screens.lost.PerrosPerdidosScreen
import com.huellitas.app.ui.screens.match.MatchScreen
import com.huellitas.app.ui.screens.donations.DonarArticulosScreen
import com.huellitas.app.ui.screens.donations.ApoyoEconomicoScreen
import com.huellitas.app.ui.screens.impacto.ImpactoScreen

// Rutas de navegación
sealed class Pantalla {
    object Login         : Pantalla()
    object Registro      : Pantalla()
    object Principal     : Pantalla()
    object Perfil        : Pantalla()
    object Adopciones    : Pantalla()
    object DetalleAdopcion : Pantalla()
    object FormularioAdopcion : Pantalla()
    object MapaAlbergues : Pantalla()
    object PerrosPerdidos : Pantalla()
    object Match         : Pantalla()
    object DonarArticulos : Pantalla()
    object FormularioDonacion : Pantalla()
    object ApoyoEconomico : Pantalla()
    object Impacto        : Pantalla()
    object PublicarPerro  : Pantalla()
    object AdminAlbergue : Pantalla()
    object MisSolicitudes : Pantalla()
    object SolicitudExito : Pantalla()
    object HistoriasAdopcion : Pantalla()
    object PublicarHistoria  : Pantalla()
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
            BackHandler { pantallaActual = Pantalla.Login }
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
                onIrAMapa = { pantallaActual = Pantalla.MapaAlbergues },
                onIrAPerrosPerdidos = { pantallaActual = Pantalla.PerrosPerdidos },
                onIrAMatch = { pantallaActual = Pantalla.Match },
                onIrADonaciones = { pantallaActual = Pantalla.DonarArticulos },
                onIrAApoyo = { pantallaActual = Pantalla.ApoyoEconomico },
                onCerrarSesion = {
                    usuarioActual = null
                    pantallaActual = Pantalla.Login
                }
            )
        }

        is Pantalla.DonarArticulos -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            DonarArticulosScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Principal },
                onIrAFormulario = { pantallaActual = Pantalla.FormularioDonacion },
                onNavigateToCatalogo = { pantallaActual = Pantalla.Adopciones },
                onNavigateToPerdidos = { pantallaActual = Pantalla.PerrosPerdidos },
                onNavigateToImpacto = { pantallaActual = Pantalla.Impacto }
            )
        }

        is Pantalla.FormularioDonacion -> {
            BackHandler { pantallaActual = Pantalla.DonarArticulos }
            com.huellitas.app.ui.screens.donations.FormularioDonacionScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.DonarArticulos }
            )
        }

        is Pantalla.ApoyoEconomico -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            ApoyoEconomicoScreen(
                onVolver = { pantallaActual = Pantalla.Principal }
            )
        }

        is Pantalla.MapaAlbergues -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            MapaAlberguesScreen(
                onVolver = { pantallaActual = Pantalla.Principal }
            )
        }

        is Pantalla.PerrosPerdidos -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            PerrosPerdidosScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Principal },
                onNavigateToCatalogo = { pantallaActual = Pantalla.Adopciones },
                onNavigateToCircular = { pantallaActual = Pantalla.DonarArticulos },
                onNavigateToImpacto = { pantallaActual = Pantalla.Impacto },
                onIrAPerfil = { pantallaActual = Pantalla.Perfil }
            )
        }

        is Pantalla.Match -> {
            MatchScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Principal },
                onIrACatalogo = { pantallaActual = Pantalla.Adopciones },
                onIrAPerfil = { pantallaActual = Pantalla.Perfil }
            )
        }

        is Pantalla.Adopciones -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            AdopcionListScreen(
                usuario = usuarioActual!!,
                onVerDetalle = { perro ->
                    perroSeleccionado = perro
                    pantallaActual = Pantalla.DetalleAdopcion
                },
                onNavigateToCircular = { pantallaActual = Pantalla.DonarArticulos },
                onNavigateToImpacto = { pantallaActual = Pantalla.Impacto },
                onNavigateToPublicar = { 
                    perroSeleccionado = null
                    pantallaActual = Pantalla.PublicarPerro 
                },
                onNavigateToPerdidos = { pantallaActual = Pantalla.PerrosPerdidos },
                onVerHistorias = { pantallaActual = Pantalla.HistoriasAdopcion }
            )
        }

        is Pantalla.HistoriasAdopcion -> {
            BackHandler { pantallaActual = Pantalla.Adopciones }
            com.huellitas.app.ui.screens.adoption.HistoriasAdopcionScreen(
                onVolver = { pantallaActual = Pantalla.Adopciones },
                onIrAPublicar = { pantallaActual = Pantalla.PublicarHistoria }
            )
        }

        is Pantalla.PublicarHistoria -> {
            BackHandler { pantallaActual = Pantalla.HistoriasAdopcion }
            com.huellitas.app.ui.screens.adoption.PublicarHistoriaScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.HistoriasAdopcion },
                onExito = { pantallaActual = Pantalla.HistoriasAdopcion }
            )
        }

        is Pantalla.PublicarPerro -> {
            BackHandler { 
                perroSeleccionado = null
                pantallaActual = Pantalla.Adopciones 
            }
            PublicarPerroScreen(
                usuario = usuarioActual!!,
                perroAEditar = perroSeleccionado,
                onVolver = { 
                    perroSeleccionado = null
                    pantallaActual = Pantalla.Adopciones 
                }
            )
        }

        is Pantalla.AdminAlbergue -> {
            BackHandler { pantallaActual = Pantalla.Perfil }
            com.huellitas.app.ui.screens.albergue.AdminAlbergueScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Perfil },
                onEditarMascota = { perro ->
                    perroSeleccionado = perro
                    pantallaActual = Pantalla.PublicarPerro
                }
            )
        }

        is Pantalla.Impacto -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            ImpactoScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Principal },
                onNavigateToCatalogo = { pantallaActual = Pantalla.Adopciones },
                onNavigateToPerdidos = { pantallaActual = Pantalla.PerrosPerdidos },
                onNavigateToCircular = { pantallaActual = Pantalla.DonarArticulos },
                onIrAPerfil = { pantallaActual = Pantalla.Perfil }
            )
        }

        is Pantalla.DetalleAdopcion -> {
            perroSeleccionado?.let { perro ->
                BackHandler { pantallaActual = Pantalla.Adopciones }
                DetalleAdopcionScreen(
                    perro     = perro,
                    usuario   = usuarioActual!!,
                    onVolver  = { pantallaActual = Pantalla.Adopciones },
                    onIrAFormulario = { pantallaActual = Pantalla.FormularioAdopcion }
                )
            }
        }

        is Pantalla.FormularioAdopcion -> {
            perroSeleccionado?.let { perro ->
                BackHandler { pantallaActual = Pantalla.DetalleAdopcion }
                com.huellitas.app.ui.screens.adoption.FormularioAdopcionScreen(
                    perro = perro,
                    usuario = usuarioActual!!,
                    onVolver = { pantallaActual = Pantalla.DetalleAdopcion },
                    onFinalizar = { pantallaActual = Pantalla.SolicitudExito }
                )
            }
        }

        is Pantalla.SolicitudExito -> {
            perroSeleccionado?.let { perro ->
                BackHandler { pantallaActual = Pantalla.Adopciones }
                com.huellitas.app.ui.screens.adoption.SolicitudExitoScreen(
                    perro = perro,
                    onVolverInicio = { pantallaActual = Pantalla.Adopciones },
                    onVerHistorias = { pantallaActual = Pantalla.HistoriasAdopcion }
                )
            }
        }

        is Pantalla.Perfil -> {
            BackHandler { pantallaActual = Pantalla.Principal }
            PerfilScreen(
                usuario            = usuarioActual!!,
                onActualizarUsuario = { actualizado -> usuarioActual = actualizado },
                onIrAAdmin         = { pantallaActual = Pantalla.AdminAlbergue },
                onIrAMisSolicitudes = { pantallaActual = Pantalla.MisSolicitudes },
                onCerrarSesion     = {
                    usuarioActual = null
                    pantallaActual = Pantalla.Login
                }
            )
        }

        is Pantalla.MisSolicitudes -> {
            BackHandler { pantallaActual = Pantalla.Perfil }
            com.huellitas.app.ui.screens.adoption.MisSolicitudesScreen(
                usuario = usuarioActual!!,
                onVolver = { pantallaActual = Pantalla.Perfil },
                onVerDetallePerro = { perroId ->
                    // Opcional: navegar al detalle del perro desde la solicitud
                }
            )
        }
    }
}

