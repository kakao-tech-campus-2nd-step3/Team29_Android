package com.iguana.data.utils

import com.iguana.data.remote.model.*
import org.json.JSONArray
import org.json.JSONObject

fun parseGetFolderContentResponseDtoList(jsonString: String): List<GetFolderContentResponseDto> {
    val list = mutableListOf<GetFolderContentResponseDto>()
    val jsonArray = JSONArray(jsonString)

    for (i in 0 until jsonArray.length()) {
        val itemObject = jsonArray.getJSONObject(i)
        val folderAndDocumentResponseType = itemObject.getString("folderAndDocumentResponseType")

        val responseObject = itemObject.optJSONObject("response")
        val response: FolderOrDocumentResponseDto? = when (folderAndDocumentResponseType) {
            "FOLDER" -> responseObject?.let {
                FolderResponseDto(
                    id = it.getLong("id"),
                    parentId = if (it.isNull("parentId")) null else it.getLong("parentId"),
                    name = it.getString("name")
                )
            }
            "DOCUMENT" -> responseObject?.let {
                DocumentResponseDto(
                    id = it.getLong("id"),
                    name = it.getString("name"),
                    url = it.getString("url")
                )
            }
            else -> null
        }

        val dto = GetFolderContentResponseDto(
            response = response,
            folderAndDocumentResponseType = folderAndDocumentResponseType
        )
        list.add(dto)
    }
    return list
}