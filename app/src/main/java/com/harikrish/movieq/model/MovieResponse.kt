package com.harikrish.movieq.model

data class MovieResponse(
    val page: Int,
    val results: MutableList<Result>,
    val totalPages: Int,
    val totalResults: Int
)