package com.huellitas.app.ui.screens.maps

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.huellitas.app.data.model.Usuario
import com.huellitas.app.data.repository.HuellitasRepository
import com.huellitas.app.ui.theme.CremaBg
import com.huellitas.app.ui.theme.GrisTexto
import com.huellitas.app.ui.theme.NaranjaHuellitas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaAlberguesScreen(
    onVolver: () -> Unit
) {
    val repo = remember { HuellitasRepository() }
    val context = LocalContext.current
    var albergues by remember { mutableStateOf(emptyList<Usuario>()) }
    var albergueSeleccionado by remember { mutableStateOf<Usuario?>(null) }

    // Lima, Perú por defecto
    val lima = LatLng(-12.046374, -77.042793)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 11f)
    }

    LaunchedEffect(Unit) {
        albergues = repo.obtenerAlbergues()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Albergues cercanos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremaBg)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                albergues.forEach { albergue ->
                    if (albergue.latitud != null && albergue.longitud != null) {
                        Marker(
                            state = MarkerState(position = LatLng(albergue.latitud, albergue.longitud)),
                            title = albergue.nombre,
                            snippet = albergue.direccion,
                            onClick = {
                                albergueSeleccionado = albergue
                                false
                            }
                        )
                    }
                }
            }

            // Card informativa al seleccionar un albergue
            albergueSeleccionado?.let { albergue ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(albergue.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrisTexto)
                        if (albergue.direccion.isNotBlank()) {
                            Text(albergue.direccion, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${albergue.latitud},${albergue.longitud}")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.setPackage("com.google.android.apps.maps")
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellitas)
                        ) {
                            Icon(Icons.Default.Navigation, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cómo llegar")
                        }
                    }
                }
            }
        }
    }
}
