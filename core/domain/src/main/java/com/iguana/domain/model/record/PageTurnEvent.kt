package com.iguana.domain.model.record

data class PageTurnEvent (
    val documentId: Long,
    val pageNumber: Int,
    val timestamp: Long,
)