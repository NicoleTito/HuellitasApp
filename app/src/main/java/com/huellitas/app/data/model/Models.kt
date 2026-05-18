package com.huellitas.app.data.model

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String = "dueño",       // "dueño", "voluntario", "albergue"
    val telefono: String = "",
    val fotoUri: String = "",
    val fechaRegistro: String = ""
)

data class PerroAdopcion(
    val id: Int = 0,
    val nombre: String,
    val raza: String = "",
    val color: String = "",
    val edad: String = "",
    val descripcion: String = "",
    val fotoUri: String = "",
    val estado: String = "disponible",  // "disponible", "en_proceso", "adoptado"
    val albergueId: Int = 0,
    val albergueNombre: String = "",
    val zona: String = "",
    val fechaPublicacion: String = ""
)

data class SolicitudAdopcion(
    val id: Int = 0,
    val perroId: Int,
    val usuarioId: Int,
    val estado: String = "pendiente",   // "pendiente", "aprobada", "rechazada"
    val fecha: String = "",
    val mensaje: String = ""
)

