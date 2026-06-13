package com.huellitas.app.data.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = "dueño",       // "dueño", "voluntario", "albergue"
    val telefono: String = "",
    val fotoUri: String = "",
    val fechaRegistro: String = ""
)

data class PerroAdopcion(
    val id: String = "",
    val nombre: String = "",
    val raza: String = "",
    val color: String = "",
    val edad: String = "",
    val descripcion: String = "",
    val fotoUri: String = "",
    val estado: String = "disponible",  // "disponible", "en_proceso", "adoptado"
    val albergueId: String = "",
    val albergueNombre: String = "",
    val zona: String = "",
    val fechaPublicacion: String = ""
)

data class SolicitudAdopcion(
    val id: String = "",
    val perroId: String = "",
    val usuarioId: String = "",
    val estado: String = "pendiente",   // "pendiente", "aprobada", "rechazada"
    val fecha: String = "",
    val mensaje: String = ""
)
