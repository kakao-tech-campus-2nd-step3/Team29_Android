package com.iguana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iguana.data.local.entity.AnnotationEntity
import com.iguana.data.local.entity.SyncStatus

@Dao
interface AnnotationDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnnotation(annotation: AnnotationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAnnotations(annotations: List<AnnotationEntity>): List<Long>

    @Query("SELECT * FROM annotations WHERE documentId = :documentId AND pageNumber = :pageNumber")
    fun getAnnotationsByPage(documentId: Long, pageNumber: Int): List<AnnotationEntity>

    @Query("DELETE FROM annotations WHERE id = :id")
    fun deleteAnnotation(id: Long)
    @Query("UPDATE annotations SET content = :content, xPosition = :xPosition, yPosition = :yPosition, width = :width, height = :height WHERE id = :id")
    fun updateAnnotation(id: Long, content: String, xPosition: Float, yPosition: Float, width: Float, height: Float)

    @Query("UPDATE annotations SET syncStatus = :syncStatus WHERE id = :id")
    fun updateSyncStatus(id: Long, syncStatus: com.iguana.domain.model.SyncStatus)

    @Query("UPDATE annotations SET id = :serverId WHERE id = :localId")
    fun updateAnnotationId(localId: Long, serverId: Long)

    @Query("DELETE FROM annotations WHERE documentId = :documentId")
    fun deleteAnnotationsByDocument(documentId: Long)

    @Query("DELETE FROM annotations")
    fun clearAnnotations()

    @Query("SELECT * FROM annotations WHERE syncStatus = :status")
    fun getAnnotationsBySyncStatus(status: SyncStatus = SyncStatus.FAILED): List<AnnotationEntity>
}
