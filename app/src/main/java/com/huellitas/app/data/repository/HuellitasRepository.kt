package com.huellitas.app.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.huellitas.app.data.model.PerroAdopcion
import com.huellitas.app.data.model.SolicitudAdopcion
import com.huellitas.app.data.model.Usuario
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HuellitasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    // ─────────────────────────────────────────
    //  PERROS EN ADOPCIÓN
    // ─────────────────────────────────────────

    suspend fun obtenerPerrosDisponibles(): List<PerroAdopcion> {
        return try {
            val snapshot = db.collection("pets")
                .whereNotEqualTo("estado", "adoptado")
                .orderBy("estado")
                .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                .get().await()
            snapshot.toObjects(PerroAdopcion::class.java)
        } catch (e: Exception) {
            emptyList()
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

    // ─────────────────────────────────────────
    //  SOLICITUDES DE ADOPCIÓN
    // ─────────────────────────────────────────

    suspend fun crearSolicitud(solicitud: SolicitudAdopcion): Result<String> {
        return try {
            val activas = contarSolicitudesActivas(solicitud.usuarioId)
            if (activas >= 3) {
                return Result.failure(Exception("No puedes tener más de 3 solicitudes activas."))
            }
            val docRef = db.collection("requests").document()
            val finalSol = solicitud.copy(id = docRef.id, fecha = fechaActual())
            docRef.set(finalSol).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerSolicitudesDeUsuario(usuarioId: String): List<SolicitudAdopcion> {
        return try {
            val snapshot = db.collection("requests")
                .whereEqualTo("usuarioId", usuarioId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get().await()
            snapshot.toObjects(SolicitudAdopcion::class.java)
        } catch (e: Exception) {
            emptyList()
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
    //  FIREBASE STORAGE (IMÁGENES)
    // ─────────────────────────────────────────

    suspend fun subirImagen(uri: Uri, path: String): String? {
        return try {
            val fileName = UUID.randomUUID().toString()
            val ref = storage.reference.child("$path/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun fechaActual(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
