package com.huellitas.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.huellitas.app.data.model.ArticuloDonacion
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.PerroPerdido
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.model.Usuario
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class HuellitasRepository(context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        // Inicializar Cloudinary solo si hay un contexto y no ha sido inicializado
        context?.let {
            try {
                val config = mapOf(
                    "cloud_name" to "dfcwpgbjr",
                    "api_key" to "224618611948594",
                    "api_secret" to "YV4NoMZpDlFr5O8Y-KVeCAgGZpw"
                )
                MediaManager.init(it, config)
            } catch (e: Exception) {
                // Ya inicializado
            }
        }
    }

    // ─────────────────────────────────────────
    //  SUBIR IMAGEN A CLOUDINARY
    // ─────────────────────────────────────────
    suspend fun subirImagen(uri: Uri, path: String): String? {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(uri)
                .option("folder", path)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        continuation.resume(url)
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("Cloudinary", "Error: ${error.description}")
                        continuation.resume(null)
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resume(null)
                    }
                }).dispatch()
        }
    }

    // ─────────────────────────────────────────
    //  USUARIOS
    // ─────────────────────────────────────────

    suspend fun registrarUsuario(usuario: Usuario): Result<String> {
        return try {
            val res = auth.createUserWithEmailAndPassword(usuario.email, usuario.password).await()
            val uid = res.user?.uid ?: throw Exception("No se pudo obtener el UID")
            val finalUser = usuario.copy(id = uid, fechaRegistro = fechaActual())
            db.collection("users").document(uid).set(finalUser).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUsuario(email: String, password: String): Usuario? {
        return try {
            val res = auth.signInWithEmailAndPassword(email, password).await()
            val uid = res.user?.uid ?: return null
            obtenerUsuarioPorId(uid)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario? {
        return try {
            val doc = db.collection("users").document(id).get().await()
            doc.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizarPerfil(usuario: Usuario): Boolean {
        return try {
            db.collection("users").document(usuario.id).set(usuario).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerAlbergues(): List<Usuario> {
        return try {
            val snapshot = db.collection("users")
                .whereEqualTo("rol", "albergue")
                .get().await()
            snapshot.toObjects(Usuario::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ─────────────────────────────────────────
    //  PERROS EN ADOPCIÓN
    // ─────────────────────────────────────────

    suspend fun obtenerPerrosDisponibles(): List<PerroAdopcion> {
        return try {
            val snapshot = db.collection("pets")
                .whereNotEqualTo("estado", "adoptado")
                .get().await()
            snapshot.toObjects(PerroAdopcion::class.java)
                .sortedWith(compareBy<PerroAdopcion> { it.estado }.thenByDescending { it.fechaPublicacion })
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerPerroPorId(id: String): PerroAdopcion? {
        return try {
            val doc = db.collection("pets").document(id).get().await()
            doc.toObject(PerroAdopcion::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerPerrosPorFiltro(raza: String = "", zona: String = ""): List<PerroAdopcion> {
        return try {
            var query: Query = db.collection("pets")
            if (raza.isNotBlank()) query = query.whereEqualTo("raza", raza)
            if (zona.isNotBlank()) query = query.whereEqualTo("zona", zona)
            val snapshot = query.orderBy("fechaPublicacion", Query.Direction.DESCENDING).get().await()
            snapshot.toObjects(PerroAdopcion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarPerro(perro: PerroAdopcion): String {
        val docRef = db.collection("pets").document()
        val finalPerro = perro.copy(id = docRef.id, fechaPublicacion = fechaActual())
        docRef.set(finalPerro).await()
        return docRef.id
    }

    suspend fun actualizarPerfilMascota(perro: PerroAdopcion): Boolean {
        return try {
            db.collection("pets").document(perro.id).set(perro).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarEstadoMascota(perroId: String, nuevoEstado: String): Boolean {
        return try {
            db.collection("pets").document(perroId).update("estado", nuevoEstado).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun borrarMascota(perroId: String): Boolean {
        return try {
            db.collection("pets").document(perroId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerMisMascotas(albergueId: String): List<PerroAdopcion> {
        return try {
            val snapshot = db.collection("pets")
                .whereEqualTo("albergueId", albergueId)
                .get().await()
            snapshot.toObjects(PerroAdopcion::class.java)
                .sortedByDescending { it.fechaPublicacion }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ─────────────────────────────────────────
    //  SOLICITUDES DE ADOPCIÓN
    // ─────────────────────────────────────────

    suspend fun crearSolicitud(solicitud: SolicitudAdopcion): Result<String> {
        return try {
            val activas = contarSolicitudesActivas(solicitud.usuarioId)
            if (activas >= 3) return Result.failure(Exception("Máximo 3 solicitudes activas."))
            val docRef = db.collection("requests").document()
            val finalSol = solicitud.copy(id = docRef.id, fecha = fechaActual())
            docRef.set(finalSol).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerSolicitudesRecibidas(albergueId: String): List<SolicitudAdopcion> {
        return try {
            val mascotasSnapshot = db.collection("pets")
                .whereEqualTo("albergueId", albergueId)
                .get().await()
            val mascotaIds = mascotasSnapshot.documents.map { it.id }
            if (mascotaIds.isEmpty()) return emptyList()
            val snapshot = db.collection("requests").whereIn("perroId", mascotaIds).get().await()
            snapshot.toObjects(SolicitudAdopcion::class.java).sortedByDescending { it.fecha }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerSolicitudesDeUsuario(usuarioId: String): List<SolicitudAdopcion> {
        return try {
            val snapshot = db.collection("requests")
                .whereEqualTo("usuarioId", usuarioId)
                .get().await()
            snapshot.toObjects(SolicitudAdopcion::class.java).sortedByDescending { it.fecha }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun actualizarEstadoSolicitud(solicitudId: String, nuevoEstado: String): Boolean {
        return try {
            db.collection("requests").document(solicitudId).update("estado", nuevoEstado).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun contarSolicitudesActivas(usuarioId: String): Int {
        val snapshot = db.collection("requests")
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("estado", "pendiente")
            .get().await()
        return snapshot.size()
    }

    // ─────────────────────────────────────────
    //  DONACIONES
    // ─────────────────────────────────────────

    suspend fun obtenerDonaciones(categoria: String = "Todo"): List<ArticuloDonacion> {
        return try {
            var query: Query = db.collection("donations")
            if (categoria != "Todo") {
                query = query.whereEqualTo("categoria", categoria)
            }
            val snapshot = query.get().await()
            snapshot.toObjects(ArticuloDonacion::class.java).sortedByDescending { it.fechaPublicacion }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarDonacion(donacion: ArticuloDonacion): String {
        val docRef = db.collection("donations").document()
        val finalDonacion = donacion.copy(id = docRef.id, fechaPublicacion = fechaActual())
        docRef.set(finalDonacion).await()
        return docRef.id
    }

    // ─────────────────────────────────────────
    //  PERROS PERDIDOS
    // ─────────────────────────────────────────

    suspend fun obtenerPerrosPerdidos(): List<PerroPerdido> {
        return try {
            val snapshot = db.collection("lost_pets")
                .get().await()
            snapshot.toObjects(PerroPerdido::class.java).sortedByDescending { it.fechaPerdido }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun reportarPerroPerdido(perro: PerroPerdido): String {
        val docRef = db.collection("lost_pets").document()
        val finalPerro = perro.copy(id = docRef.id, fechaPerdido = fechaActual())
        docRef.set(finalPerro).await()
        return docRef.id
    }

    // ─────────────────────────────────────────
    //  IMPACTO (CONTEOS)
    // ─────────────────────────────────────────

    suspend fun obtenerConteoDonacionesUsuario(usuarioId: String): Int {
        return try {
            val snapshot = db.collection("donations")
                .whereEqualTo("donanteId", usuarioId)
                .get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun obtenerConteoAdopcionesUsuario(usuarioId: String): Int {
        return try {
            val snapshot = db.collection("requests")
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("estado", "aprobada")
                .get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun obtenerConteoPerrosPerdidosUsuario(usuarioId: String): Int {
        return try {
            val snapshot = db.collection("lost_pets")
                .whereEqualTo("reporteroId", usuarioId)
                .get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    private fun fechaActual(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
