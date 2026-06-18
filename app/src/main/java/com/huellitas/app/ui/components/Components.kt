package com.huellitas.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huellitas.app.ui.theme.*

// ─── Botón principal naranja ─────────────────────────────
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick     = onClick,
        enabled     = habilitado,
        modifier    = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape       = RoundedCornerShape(14.dp),
        colors      = ButtonDefaults.buttonColors(
            containerColor         = NaranjaHuellitas,
            contentColor           = BlancoPuro,
            disabledContainerColor = NaranjaHuellitas.copy(alpha = 0.4f)
        )
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

// ─── Botón secundario (borde) ────────────────────────────
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = NaranjaHuellitas),
        border   = androidx.compose.foundation.BorderStroke(1.5.dp, NaranjaHuellitas)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

// ─── Campo de texto estilizado ───────────────────────────
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    esPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    mensajeError: String = ""
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value         = valor,
            onValueChange = onValorChange,
            label         = { Text(etiqueta) },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            isError       = isError,
            leadingIcon   = leadingIcon,
            trailingIcon  = trailingIcon,
            visualTransformation = if (esPassword)
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = if (esPassword)
                androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                )
            else
                androidx.compose.foundation.text.KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = NaranjaHuellitas,
                focusedLabelColor    = NaranjaHuellitas,
                cursorColor          = NaranjaHuellitas
            ),
            singleLine = true
        )
        if (isError && mensajeError.isNotBlank()) {
            Text(
                text     = mensajeError,
                color    = RojoError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

// ─── Chip de estado del perro ────────────────────────────
@Composable
fun ChipEstado(estado: String) {
    val (bg, fg, label) = when (estado) {
        "disponible" -> Triple(Color(0xFFE8F5E9), VerdeExito, "Disponible")
        "en_proceso" -> Triple(Color(0xFFFFF8E1), AmarilloAlerta, "En proceso")
        "adoptado"   -> Triple(Color(0xFFEDE7F6), Color(0xFF7E57C2), "Adoptado")
        else         -> Triple(NaranjaClaro, NaranjaHuellitas, estado)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Header con logo de huella ───────────────────────────
@Composable
fun LogoHuellitas(modifier: Modifier = Modifier) {
    Row(
        modifier         = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier        = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(NaranjaHuellitas),
            contentAlignment = Alignment.Center
        ) {
            Text("🐾", fontSize = 22.sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            "Huellitas",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            color      = NaranjaHuellitas
        )
    }
}

// ─── Tarjeta de sección ──────────────────────────────────
@Composable
fun TarjetaSeccion(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content  = { Column(Modifier.padding(16.dp), content = content) }
    )
}

// ─── Indicador de carga ──────────────────────────────────
@Composable
fun CargandoIndicador() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = NaranjaHuellitas)
    }
}

// ─── Mensaje vacío ───────────────────────────────────────
@Composable
fun MensajeVacio(mensaje: String, emoji: String = "🐾") {
    Column(
        modifier              = Modifier.fillMaxSize(),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        Text(emoji, fontSize = 52.sp)
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = GrisSecundario, fontSize = 15.sp)
    }
}

