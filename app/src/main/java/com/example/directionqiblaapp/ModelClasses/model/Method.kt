package com.example.qibla.api.model

data class Method(
    val id: Int,
    val location: Location,
    val name: String,
    val params: Params
)