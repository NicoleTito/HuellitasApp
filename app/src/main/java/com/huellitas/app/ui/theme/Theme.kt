package com.huellitas.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Colores Huellitas ───────────────────────────────────
val NaranjaHuellitas  = Color(0xFFE8612C)   // principal – cálido y enérgico
val NaranjaClaro      = Color(0xFFFFF0E8)   // fondo suave
val MarronSuave       = Color(0xFF7B4F2E)   // acento oscuro
val CremaBg           = Color(0xFFFBF7F4)   // fondo general
val GrisTexto         = Color(0xFF3D2C22)   // texto principal
val GrisSecundario    = Color(0xFF9E8878)   // texto secundario
val VerdeExito        = Color(0xFF4CAF50)
val AmarilloAlerta    = Color(0xFFFFC107)
val RojoError         = Color(0xFFE53935)
val BlancoPuro        = Color(0xFFFFFFFF)

// --- Colores para Tarjetas del Menú ---
val ColorMatchBg      = Color(0xFFE67E5D) // Naranja coral
val ColorDonarBg      = Color(0xFFD7E5A4) // Verde lima suave
val ColorDonarTexto   = Color(0xFF5A6632) // Verde oscuro
val ColorIconoAdoptar = Color(0xFFFDE7E1) // Rosado suave lupa
val ColorIconoApoyo   = Color(0xFFE8EAF6) // Gris azulado mano



private val HuellitasColorScheme = lightColorScheme(
    primary          = NaranjaHuellitas,
    onPrimary        = BlancoPuro,
    primaryContainer = NaranjaClaro,
    secondary        = MarronSuave,
    onSecondary      = BlancoPuro,
    background       = CremaBg,
    surface          = BlancoPuro,
    onBackground     = GrisTexto,
    onSurface        = GrisTexto,
    error            = RojoError
)

// ─── Typography ──────────────────────────────────────────
// Nota: agrega las fuentes en res/font/ y ajusta los nombres si las descargás
val HuellitasTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        color      = GrisTexto
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        color      = GrisTexto
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        color      = GrisTexto
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        color      = GrisTexto
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        color      = GrisSecundario
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun HuellitasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HuellitasColorScheme,
        typography  = HuellitasTypography,
        content     = content
    )
}