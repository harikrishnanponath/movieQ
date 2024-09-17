package com.harikrish.movieq.api

import com.harikrish.movieq.model.MovieResponse
import com.harikrish.movieq.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey:String = API_KEY
    ) : Response<MovieResponse>

    @GET("/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey:String = API_KEY
    ) : Response<MovieResponse>

}