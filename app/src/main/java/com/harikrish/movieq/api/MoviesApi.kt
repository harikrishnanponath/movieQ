package com.harikrish.movieq.api

import com.harikrish.movieq.model.MovieResponse
import com.harikrish.movieq.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") pageNumber: Int = 1,
        @Query("api_key") apiKey:String = API_KEY
    ) : Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") pageNumber: Int = 1,
        @Query("api_key") apiKey:String = API_KEY
    ) : Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("page") pageNumber: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("query") searchKey: String
    ) : Response<MovieResponse>


}