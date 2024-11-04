package com.iguana.domain.model

data class Annotation(
    val id: Long, // 고유 식별자
    val pageNumber: Int,
    val content: String, // 내용
    val x: Float, // x 좌표
    val y: Float, // y 좌표
    val width: Float, // 너비
    val height: Float // 높이
)
