package com.iguana.domain.model

data class Annotation(
    val id: Long? = null, // 주석 ID (필요할 지 안할 지는 추후 결정)
    val pageNumber: Int,
    val content: String, // 내용
    val x: Float, // x 좌표
    val y: Float, // y 좌표
    val width: Float, // 너비
    val height: Float // 높이
)
