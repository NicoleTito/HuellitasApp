package com.huellitas.app.data.repository

import android.content.ContentValues
import android.content.Context
import com.huellitas.app.data.database.DatabaseHelper
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_ALBERGUE_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_COLOR
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_DESCRIPCION
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_EDAD
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_ESTADO
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_FECHA
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_FOTO
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_NOMBRE
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_RAZA
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_DOG_ZONA
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_ESTADO
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_FECHA
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_MENSAJE
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_PERRO_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_REQ_USUARIO_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_EMAIL
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_FECHA_REG
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_FOTO
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_ID
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_NOMBRE
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_PASSWORD
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_ROL
import com.huellitas.app.data.database.DatabaseHelper.Companion.COL_USER_TELEFONO
import com.huellitas.app.data.database.DatabaseHelper.Companion.TABLE_ADOPTION_REQUESTS
import com.huellitas.app.data.database.DatabaseHelper.Companion.TABLE_DOGS
import com.huellitas.app.data.database.DatabaseHelper.Companion.TABLE_USERS
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HuellitasRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ─────────────────────────────────────────
    //  USUARIOS
    // ─────────────────────────────────────────

    fun registrarUsuario(usuario: Usuario): Result<Long> {
        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(COL_USER_NOMBRE, usuario.nombre)
                put(COL_USER_EMAIL, usuario.email)
                put(COL_USER_PASSWORD, usuario.password)
                put(COL_USER_ROL, usuario.rol)
                put(COL_USER_TELEFONO, usuario.telefono)
                put(COL_USER_FOTO, usuario.fotoUri)
                put(COL_USER_FECHA_REG, fechaActual())
            }
            val id = db.insert(TABLE_USERS, null, values)
            if (id == -1L) Result.failure(Exception("No se pudo registrar el usuario"))
            else Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loginUsuario(email: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USER_EMAIL = ? AND $COL_USER_PASSWORD = ?",
            arrayOf(email, password),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val usuario = cursorToUsuario(cursor)
            cursor.close()
            usuario
        } else {
            cursor.close()
            null
        }
    }

    fun obtenerUsuarioPorId(id: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_USERS, null,
            "$COL_USER_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val u = cursorToUsuario(cursor)
            cursor.close()
            u
        } else {
            cursor.close()
            null
        }
    }

    fun actualizarPerfil(usuario: Usuario): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NOMBRE, usuario.nombre)
            put(COL_USER_TELEFONO, usuario.telefono)
            put(COL_USER_FOTO, usuario.fotoUri)
        }
        val rows = db.update(TABLE_USERS, values, "$COL_USER_ID = ?", arrayOf(usuario.id.toString()))
        return rows > 0
    }

    fun emailExiste(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COL_USER_ID),
            "$COL_USER_EMAIL = ?", arrayOf(email), null, null, null)
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // ─────────────────────────────────────────
    //  PERROS EN ADOPCIÓN
    // ─────────────────────────────────────────

    fun obtenerPerrosDisponibles(): List<PerroAdopcion> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.*, u.$COL_USER_NOMBRE AS albergue_nombre
            FROM $TABLE_DOGS d
            LEFT JOIN $TABLE_USERS u ON d.$COL_DOG_ALBERGUE_ID = u.$COL_USER_ID
            WHERE d.$COL_DOG_ESTADO != 'adoptado'
            ORDER BY d.$COL_DOG_FECHA DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        val lista = mutableListOf<PerroAdopcion>()
        while (cursor.moveToNext()) {
            lista.add(cursorToPerro(cursor))
        }
        cursor.close()
        return lista
    }

    fun obtenerPerrosPorFiltro(raza: String = "", zona: String = "", estado: String = ""): List<PerroAdopcion> {
        val db = dbHelper.readableDatabase
        val condiciones = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (raza.isNotBlank()) { condiciones.add("$COL_DOG_RAZA LIKE ?"); args.add("%$raza%") }
        if (zona.isNotBlank()) { condiciones.add("$COL_DOG_ZONA LIKE ?"); args.add("%$zona%") }
        if (estado.isNotBlank()) { condiciones.add("$COL_DOG_ESTADO = ?"); args.add(estado) }

        val where = if (condiciones.isEmpty()) "1=1" else condiciones.joinToString(" AND ")
        val cursor = db.query(TABLE_DOGS, null, where, args.toTypedArray(), null, null, "$COL_DOG_FECHA DESC")
        val lista = mutableListOf<PerroAdopcion>()
        while (cursor.moveToNext()) lista.add(cursorToPerro(cursor))
        cursor.close()
        return lista
    }

    fun agregarPerro(perro: PerroAdopcion): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COL_DOG_NOMBRE, perro.nombre)
            put(COL_DOG_RAZA, perro.raza)
            put(COL_DOG_COLOR, perro.color)
            put(COL_DOG_EDAD, perro.edad)
            put(COL_DOG_DESCRIPCION, perro.descripcion)
            put(COL_DOG_FOTO, perro.fotoUri)
            put(COL_DOG_ESTADO, perro.estado)
            put(COL_DOG_ALBERGUE_ID, perro.albergueId)
            put(COL_DOG_ZONA, perro.zona)
            put(COL_DOG_FECHA, fechaActual())
        }
        return db.insert(TABLE_DOGS, null, values)
    }

    // ─────────────────────────────────────────
    //  SOLICITUDES DE ADOPCIÓN
    // ─────────────────────────────────────────

    fun crearSolicitud(solicitud: SolicitudAdopcion): Result<Long> {
        // Regla de negocio: máximo 3 solicitudes simultáneas pendientes
        val activas = contarSolicitudesActivas(solicitud.usuarioId)
        if (activas >= 3) {
            return Result.failure(Exception("No puedes tener más de 3 solicitudes de adopción activas simultáneamente."))
        }
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COL_REQ_PERRO_ID, solicitud.perroId)
            put(COL_REQ_USUARIO_ID, solicitud.usuarioId)
            put(COL_REQ_ESTADO, "pendiente")
            put(COL_REQ_FECHA, fechaActual())
            put(COL_REQ_MENSAJE, solicitud.mensaje)
        }
        val id = db.insert(TABLE_ADOPTION_REQUESTS, null, values)
        return if (id == -1L) Result.failure(Exception("Error al crear solicitud"))
        else Result.success(id)
    }

    fun obtenerSolicitudesDeUsuario(usuarioId: Int): List<SolicitudAdopcion> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_ADOPTION_REQUESTS, null,
            "$COL_REQ_USUARIO_ID = ?", arrayOf(usuarioId.toString()),
            null, null, "$COL_REQ_FECHA DESC"
        )
        val lista = mutableListOf<SolicitudAdopcion>()
        while (cursor.moveToNext()) lista.add(cursorToSolicitud(cursor))
        cursor.close()
        return lista
    }

    private fun contarSolicitudesActivas(usuarioId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_ADOPTION_REQUESTS, arrayOf(COL_REQ_ID),
            "$COL_REQ_USUARIO_ID = ? AND $COL_REQ_ESTADO = 'pendiente'",
            arrayOf(usuarioId.toString()), null, null, null
        )
        val count = cursor.count
        cursor.close()
        return count
    }

    // ─────────────────────────────────────────
    //  HELPERS CURSOR
    // ─────────────────────────────────────────

    private fun cursorToUsuario(cursor: android.database.Cursor) = Usuario(
        id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
        nombre   = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NOMBRE)) ?: "",
        email    = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)) ?: "",
        password = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)) ?: "",
        rol      = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROL)) ?: "dueño",
        telefono = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_TELEFONO)) ?: "",
        fotoUri  = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FOTO)) ?: "",
        fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FECHA_REG)) ?: ""
    )

    private fun cursorToPerro(cursor: android.database.Cursor): PerroAdopcion {
        val albergueNombreIdx = cursor.getColumnIndex("albergue_nombre")
        return PerroAdopcion(
            id           = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOG_ID)),
            nombre       = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_NOMBRE)) ?: "",
            raza         = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_RAZA)) ?: "",
            color        = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_COLOR)) ?: "",
            edad         = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_EDAD)) ?: "",
            descripcion  = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_DESCRIPCION)) ?: "",
            fotoUri      = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_FOTO)) ?: "",
            estado       = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_ESTADO)) ?: "disponible",
            albergueId   = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOG_ALBERGUE_ID)),
            albergueNombre = if (albergueNombreIdx >= 0) cursor.getString(albergueNombreIdx) ?: "" else "",
            zona         = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_ZONA)) ?: "",
            fechaPublicacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_FECHA)) ?: ""
        )
    }

    private fun cursorToSolicitud(cursor: android.database.Cursor) = SolicitudAdopcion(
        id         = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REQ_ID)),
        perroId    = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REQ_PERRO_ID)),
        usuarioId  = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REQ_USUARIO_ID)),
        estado     = cursor.getString(cursor.getColumnIndexOrThrow(COL_REQ_ESTADO)) ?: "pendiente",
        fecha      = cursor.getString(cursor.getColumnIndexOrThrow(COL_REQ_FECHA)) ?: "",
        mensaje    = cursor.getString(cursor.getColumnIndexOrThrow(COL_REQ_MENSAJE)) ?: ""
    )

    private fun fechaActual(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

