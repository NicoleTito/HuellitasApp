package com.huellitas.app.data.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = "dueño",       // "dueño", "voluntario", "albergue"
    val telefono: String = "",
    val fotoUri: String = "",
    val fechaRegistro: String = "",
    // Coordenadas para albergues
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccion: String = ""
)

data class PerroAdopcion(
    val id: String = "",
    val nombre: String = "",
    val especie: String = "Perro",    // "Perro", "Gato", "Otro"
    val raza: String = "",
    val tamano: String = "Mediano",  // "Pequeño", "Mediano", "Grande"
    val sexo: String = "Macho",      // "Macho", "Hembra"
    val energia: String = "Media",   // "Baja", "Media", "Alta"
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
    val mensaje: String = "",
    // Campos nuevos del formulario
    val nombreCompleto: String = "",
    val direccion: String = "",
    val tipoVivienda: String = "",
    val tieneMascotas: Boolean = false,
    val infoMascotas: String = "",
    val compromiso: Boolean = false,
    val firma: String = "",
    // Campos del paso 2 (Estilo de Vida)
    val tiempoSola: String = "",
    val responsable: String = "",
    val presupuestoOk: Boolean = false
)

data class PerroPerdido(
    val id: String = "",
    val nombre: String = "",
    val especie: String = "Perro",
    val raza: String = "",
    val tamano: String = "Mediano",
    val color: String = "",
    val descripcion: String = "",
    val fotoUri: String = "",
    val zona: String = "",
    val contacto: String = "",
    val fechaPerdido: String = "",
    val reporteroId: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val estado: String = "perdido" // "perdido", "encontrado"
)

data class ArticuloDonacion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "", // "Camas", "Correas", "Comida", "Juguetes"
    val fotoUri: String = "",
    val donanteNombre: String = "",
    val donanteId: String = "",
    val fechaPublicacion: String = "",
    val estado: String = "disponible" // "disponible", "entregado"
)
