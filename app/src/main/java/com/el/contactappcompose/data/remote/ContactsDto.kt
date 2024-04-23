package com.el.contactappcompose.data.remote

data class ContactsListsDto(
    val results: List<ResultUsers>,
    val info: Info,
)

data class ResultUsers(
    val gender: String,
    val name: Name,
    val email: String,
    val phone: String,
    val cell: String,
    val id: Id,
    val picture: Picture,
)

data class Name(
    val title: String,
    val first: String,
    val last: String,
)

data class Id(
    val name: String,
    val value: String?,
)

data class Picture(
    val large: String,
    val medium: String,
    val thumbnail: String,
)

data class Info(
    val seed: String,
    val results: Long,
    val page: Long,
    val version: String,
)