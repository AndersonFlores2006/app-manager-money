package com.example.gestor_money.data.sync

/**
 * Interface para entidades que soportan sincronización con la nube
 */
interface SyncableEntity {
    /** ID en la base de datos local (Room) */
    val id: Long
    
    /** ID en la nube (Firebase Firestore) */
    val cloudId: String?
    
    /** Estado de sincronización */
    val syncStatus: String
    
    /** Timestamp de última modificación (para resolver conflictos) */
    val lastModified: Long
}
