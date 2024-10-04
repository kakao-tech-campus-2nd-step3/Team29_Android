package com.iguana.domain.model

data class RecentFile(
    val id: Long,
    val fileName: String,
    val fileUri: String,
    val lastOpened: Long,
    val bookmarkedPage: Int?
)
