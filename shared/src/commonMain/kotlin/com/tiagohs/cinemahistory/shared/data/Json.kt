package com.tiagohs.cinemahistory.shared.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Leitura tolerante do JSON do conteúdo (o mesmo do Android, escrito à mão e por scripts):
 * números chegam como int ou float, campos faltam, e um campo errado nunca derruba a leitura inteira.
 */
internal val json = Json { ignoreUnknownKeys = true; isLenient = true }

internal fun lerJson(texto: String): JsonElement = json.parseToJsonElement(texto)

internal fun JsonObject.texto(chave: String): String? =
    (this[chave] as? JsonPrimitive)?.takeIf { it !is JsonNull }?.content

internal fun JsonObject.textoOu(chave: String, padrao: String = ""): String = texto(chave) ?: padrao

internal fun JsonObject.inteiro(chave: String): Int? =
    (this[chave] as? JsonPrimitive)?.let { it.longOrNull?.toInt() ?: it.doubleOrNull?.toInt() ?: it.content.trim().toIntOrNull() }

internal fun JsonObject.longo(chave: String): Long? =
    (this[chave] as? JsonPrimitive)?.let { it.longOrNull ?: it.doubleOrNull?.toLong() ?: it.content.trim().toLongOrNull() }

internal fun JsonObject.decimal(chave: String): Double? = (this[chave] as? JsonPrimitive)?.doubleOrNull

internal fun JsonObject.logico(chave: String): Boolean? = (this[chave] as? JsonPrimitive)?.booleanOrNull

internal fun JsonObject.objeto(chave: String): JsonObject? = this[chave] as? JsonObject

internal fun JsonObject.lista(chave: String): List<JsonObject> =
    (this[chave] as? JsonArray)?.mapNotNull { it as? JsonObject } ?: emptyList()

internal fun JsonElement.objetos(): List<JsonObject> = (this as? JsonArray)?.mapNotNull { it as? JsonObject } ?: emptyList()
