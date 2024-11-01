package com.iguana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iguana.data.local.entity.AnnotationEntity

@Dao
interface AnnotationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnnotation(annotation: AnnotationEntity)

    @Query("SELECT * FROM annotations WHERE documentId = :documentId AND pageNumber = :pageNumber")
    fun getAnnotationsByPage(documentId: Long, pageNumber: Int): List<AnnotationEntity>

    @Query("DELETE FROM annotations WHERE id = :id")
    fun deleteAnnotation(id: Long)
    @Query("UPDATE annotations SET content = :content, xPosition = :xPosition, yPosition = :yPosition WHERE id = :id")
    fun updateAnnotation(id: Long, content: String, xPosition: Float, yPosition: Float)

    @Query("DELETE FROM annotations WHERE documentId = :documentId")
    fun deleteAnnotationsByDocument(documentId: Long)

    @Query("DELETE FROM annotations")
    fun clearAnnotations()
}
