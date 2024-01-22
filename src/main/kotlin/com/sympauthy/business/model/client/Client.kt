package com.sympauthy.business.model.client

data class Client(
    val id: String,
    val secret: String,
    val authorizedUrls: List<String> = emptyList()
)
