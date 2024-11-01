package com.iguana.domain.model.record

data class PageTurnEvent(
    val documentId: Long,
    val prevPage : Int,
    val nextPage : Int,
    val timestamp: Double
)