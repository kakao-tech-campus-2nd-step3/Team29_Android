package com.iguana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "annotations")
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: Long,        // 외래 키로 사용될 문서 ID
    val pageNumber: Int,         // 주석이 있는 페이지 번호
    val xPosition: Float,        // x 좌표
    val yPosition: Float,        // y 좌표
    val content: String,             // 주석 텍스트
    val width: Float,              // 너비
    val height: Float             // 높이
)