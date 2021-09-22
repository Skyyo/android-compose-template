package com.skyyo.compose_template.application

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "cats_table")
@JsonClass(generateAdapter = true)
data class Cat(
    @PrimaryKey
    @Json(name = "id")
    val id: String
)