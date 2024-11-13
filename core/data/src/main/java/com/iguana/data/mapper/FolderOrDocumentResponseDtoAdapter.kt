package com.iguana.data.mapper

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.iguana.data.remote.model.DocumentResponseDto
import com.iguana.data.remote.model.FolderOrDocumentResponseDto
import com.iguana.data.remote.model.FolderResponseDto
import com.iguana.data.remote.model.GetFolderContentResponseDto
import com.iguana.data.utils.Logger
import java.lang.reflect.Type

class FolderOrDocumentResponseDtoAdapter : JsonDeserializer<GetFolderContentResponseDto?> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GetFolderContentResponseDto? {
        val jsonObject = json.asJsonObject
        Log.d("testt", "jsonObject: $jsonObject")
        val typeElement = jsonObject.get("folderAndDocumentResponseType")?.asString
        Log.d("testt", "typeElement: $typeElement")
        val responseElement = jsonObject.get("response")

        // folderAndDocumentResponseType이 없거나 null일 때 null을 반환
        if (responseElement == null) {
            Log.d("testt", "responseElement is null")
            if (typeElement == null) {
                Log.d("testt", "typeElement is null")
                return null
            }
            return null
        }


        return when (typeElement) {
            "FOLDER" -> GetFolderContentResponseDto(
                folderAndDocumentResponseType = typeElement,
                response = context.deserialize(responseElement, FolderResponseDto::class.java)
            )
            "DOCUMENT" -> GetFolderContentResponseDto(
                folderAndDocumentResponseType = typeElement,
                response = context.deserialize(responseElement, DocumentResponseDto::class.java))
            else -> throw JsonParseException("Unknown type: ${typeElement}")
        }
    }
}
