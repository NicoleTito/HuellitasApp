package com.huellitas.app.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "huellitas.db"
        const val DATABASE_VERSION = 1

        // Tabla: usuarios
        const val TABLE_USERS = "usuarios"
        const val COL_USER_ID = "id"
        const val COL_USER_NOMBRE = "nombre"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROL = "rol"          // "dueño", "voluntario", "albergue"
        const val COL_USER_TELEFONO = "telefono"
        const val COL_USER_FOTO = "foto_uri"
        const val COL_USER_FECHA_REG = "fecha_registro"

        // Tabla: perros_adopcion
        const val TABLE_DOGS = "perros_adopcion"
        const val COL_DOG_ID = "id"
        const val COL_DOG_NOMBRE = "nombre"
        const val COL_DOG_RAZA = "raza"
        const val COL_DOG_COLOR = "color"
        const val COL_DOG_EDAD = "edad"
        const val COL_DOG_DESCRIPCION = "descripcion"
        const val COL_DOG_FOTO = "foto_uri"
        const val COL_DOG_ESTADO = "estado"     // "disponible", "en_proceso", "adoptado"
        const val COL_DOG_ALBERGUE_ID = "albergue_id"
        const val COL_DOG_ZONA = "zona"
        const val COL_DOG_FECHA = "fecha_publicacion"

        // Tabla: solicitudes_adopcion
        const val TABLE_ADOPTION_REQUESTS = "solicitudes_adopcion"
        const val COL_REQ_ID = "id"
        const val COL_REQ_PERRO_ID = "perro_id"
        const val COL_REQ_USUARIO_ID = "usuario_id"
        const val COL_REQ_ESTADO = "estado"     // "pendiente", "aprobada", "rechazada"
        const val COL_REQ_FECHA = "fecha_solicitud"
        const val COL_REQ_MENSAJE = "mensaje"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NOMBRE   TEXT    NOT NULL,
                $COL_USER_EMAIL    TEXT    NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT    NOT NULL,
                $COL_USER_ROL      TEXT    NOT NULL DEFAULT 'dueño',
                $COL_USER_TELEFONO TEXT,
                $COL_USER_FOTO     TEXT,
                $COL_USER_FECHA_REG TEXT   NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_DOGS (
                $COL_DOG_ID          INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DOG_NOMBRE      TEXT    NOT NULL,
                $COL_DOG_RAZA        TEXT,
                $COL_DOG_COLOR       TEXT,
                $COL_DOG_EDAD        TEXT,
                $COL_DOG_DESCRIPCION TEXT,
                $COL_DOG_FOTO        TEXT,
                $COL_DOG_ESTADO      TEXT    NOT NULL DEFAULT 'disponible',
                $COL_DOG_ALBERGUE_ID INTEGER,
                $COL_DOG_ZONA        TEXT,
                $COL_DOG_FECHA       TEXT    NOT NULL,
                FOREIGN KEY ($COL_DOG_ALBERGUE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ADOPTION_REQUESTS (
                $COL_REQ_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REQ_PERRO_ID   INTEGER NOT NULL,
                $COL_REQ_USUARIO_ID INTEGER NOT NULL,
                $COL_REQ_ESTADO     TEXT    NOT NULL DEFAULT 'pendiente',
                $COL_REQ_FECHA      TEXT    NOT NULL,
                $COL_REQ_MENSAJE    TEXT,
                FOREIGN KEY ($COL_REQ_PERRO_ID)   REFERENCES $TABLE_DOGS($COL_DOG_ID),
                FOREIGN KEY ($COL_REQ_USUARIO_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
        """.trimIndent())

        // Seed: datos de prueba
        insertSeedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADOPTION_REQUESTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertSeedData(db: SQLiteDatabase) {
        db.execSQL("""
            INSERT INTO $TABLE_USERS ($COL_USER_NOMBRE, $COL_USER_EMAIL, $COL_USER_PASSWORD, $COL_USER_ROL, $COL_USER_TELEFONO, $COL_USER_FECHA_REG)
            VALUES ('Albergue Patitas Felices', 'albergue@huellitas.pe', '1234', 'albergue', '999888777', '2024-01-10')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO $TABLE_DOGS ($COL_DOG_NOMBRE, $COL_DOG_RAZA, $COL_DOG_COLOR, $COL_DOG_EDAD, $COL_DOG_DESCRIPCION, $COL_DOG_ESTADO, $COL_DOG_ALBERGUE_ID, $COL_DOG_ZONA, $COL_DOG_FECHA)
            VALUES ('Rocky', 'Mestizo', 'Café y blanco', '2 años', 'Juguetón y cariñoso, se lleva bien con niños.', 'disponible', 1, 'San Miguel', '2024-06-01')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO $TABLE_DOGS ($COL_DOG_NOMBRE, $COL_DOG_RAZA, $COL_DOG_COLOR, $COL_DOG_EDAD, $COL_DOG_DESCRIPCION, $COL_DOG_ESTADO, $COL_DOG_ALBERGUE_ID, $COL_DOG_ZONA, $COL_DOG_FECHA)
            VALUES ('Luna', 'Labrador', 'Dorada', '1 año', 'Tranquila y obediente. Vacunas al día.', 'disponible', 1, 'Miraflores', '2024-06-03')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO $TABLE_DOGS ($COL_DOG_NOMBRE, $COL_DOG_RAZA, $COL_DOG_COLOR, $COL_DOG_EDAD, $COL_DOG_DESCRIPCION, $COL_DOG_ESTADO, $COL_DOG_ALBERGUE_ID, $COL_DOG_ZONA, $COL_DOG_FECHA)
            VALUES ('Toby', 'Beagle', 'Tricolor', '3 años', 'Le encanta correr y jugar. Ideal para casa con jardín.', 'en_proceso', 1, 'La Molina', '2024-05-20')
        """.trimIndent())
    }
}