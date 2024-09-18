package com.harikrish.movieq.repository

import com.harikrish.movieq.api.RetrofitInstance

class MoviesRepository  {

    suspend fun getPopularMovies(pageNumber: Int) =
        RetrofitInstance.api.getPopularMovies(pageNumber)

    suspend fun getUpcomingMovies(pageNumber: Int) =
        RetrofitInstance.api.getUpcomingMovies(pageNumber)

    suspend fun searchMovies(pageNumber: Int, searchKey: String) =
        RetrofitInstance.api.searchMovies(pageNumber = pageNumber, searchKey =  searchKey)
}