package com.iguana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnnotationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: Annotation)

    @Query("SELECT * FROM annotations WHERE documentId = :documentId AND pageNumber = :pageNumber")
    suspend fun getAnnotationsByPage(documentId: Long, pageNumber: Int): List<Annotation>

    @Query("DELETE FROM annotations WHERE id = :id")
    suspend fun deleteAnnotation(id: Long)

    @Query("UPDATE annotations SET text = :text, xPosition = :xPosition, yPosition = :yPosition WHERE id = :id")
    suspend fun updateAnnotation(id: Long, text: String, xPosition: Float, yPosition: Float)

    @Query("DELETE FROM annotations WHERE documentId = :documentId")
    suspend fun deleteAnnotationsByDocument(documentId: Long)
}
