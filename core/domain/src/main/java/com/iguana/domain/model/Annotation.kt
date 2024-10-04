package com.iguana.domain.model

data class Annotation(
    val id: Long? = null, // 주석 ID (필요할 지 안할 지는 추후 결정)
    val content: String, // 내용
    val x: Int, // x 좌표
    val y: Int, // y 좌표
    val width: Int, // 너비
    val height: Int, // 높이
)
