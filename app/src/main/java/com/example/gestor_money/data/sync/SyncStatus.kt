package com.example.gestor_money.data.sync

/**
 * Status de sincronización para las entidades
 */
enum class SyncStatus {
    /** Synced con la nube */
    SYNCED,
    
    /** Pendiente de subir a la nube */
    PENDING_UPLOAD,
    
    /** Pendiente de eliminar de la nube */
    PENDING_DELETE,
    
    /** Conflicto detectado (se resolverá con last-write-wins) */
    CONFLICT
}
