package com.harikrish.movieq.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.harikrish.movieq.model.MovieResponse
import com.harikrish.movieq.repository.MoviesRepository
import com.harikrish.movieq.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class MoviesViewModel(app: Application, val repository: MoviesRepository) : AndroidViewModel(app) {

    //for the popular movies
    val popularMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var popularMoviesPage = 1
    var popularMoviesResponse: MovieResponse? = null

    //for the upcoming movies
    val upcomingMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var upcomingMoviesPage = 1
    var upcomingMoviesResponse: MovieResponse? = null

    //for the search Fragment
    val searchMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var searchMoviesPage = 1
    var searchMoviesResponse: MovieResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null


    init {
        getPopularMovies()
        getUpcomingMovies()
    }

    fun searchMovies(searchQuery: String) = viewModelScope.launch {
        searchMoviesInternet(searchQuery)
    }

    fun getUpcomingMovies() = viewModelScope.launch {
        upcomingMoviesInternet()
    }

    fun getPopularMovies() = viewModelScope.launch {
        popularMoviesInternet()
    }

    private fun handleSearchMoviesResponse(response: Response<MovieResponse>) : Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchMoviesResponse == null || newSearchQuery != oldSearchQuery) {
                    searchMoviesPage = 1
                    oldSearchQuery = newSearchQuery
                    searchMoviesResponse = resultResponse
                } else {
                    searchMoviesPage++
                    val oldMovies = searchMoviesResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(searchMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }



    private fun handleUpcomingMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                upcomingMoviesPage++
                if (upcomingMoviesResponse == null) {
                    upcomingMoviesResponse = resultResponse
                } else {
                    val oldMovies = upcomingMoviesResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(upcomingMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handlePopularMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                popularMoviesPage++
                if (popularMoviesResponse == null) {
                    popularMoviesResponse = resultResponse
                } else {
                    val oldMovies = popularMoviesResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                    popularMoviesResponse = popularMoviesResponse?.copy(results = oldMovies!!)
                }
                return Resource.Success(popularMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun searchMoviesInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchMovies.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = repository.searchMovies(searchKey = searchQuery, pageNumber = searchMoviesPage)
                searchMovies.postValue(handleSearchMoviesResponse(response))
            }else {
                searchMovies.postValue(Resource.Error("No Internet Connection!"))
            }
        } catch (t : Throwable) {
            when (t) {
                is IOException -> searchMovies.postValue(Resource.Error("Unable to connect!"))
                else -> searchMovies.postValue(Resource.Error("No signal!"))
            }
        }
    }

    private suspend fun upcomingMoviesInternet() {
        upcomingMovies.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = repository.getUpcomingMovies(upcomingMoviesPage)
                upcomingMovies.postValue(handleUpcomingMoviesResponse(response))
            } else {
                upcomingMovies.postValue(Resource.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> upcomingMovies.postValue(Resource.Error("Unable to connect!"))
                else -> upcomingMovies.postValue(Resource.Error("No Signal!"))
            }
        }
    }

    private suspend fun popularMoviesInternet() {
        popularMovies.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = repository.getPopularMovies(popularMoviesPage)
                Log.d("inside", "$popularMoviesPage")
                popularMovies.postValue(handlePopularMoviesResponse(response))
            } else {
                popularMovies.postValue(Resource.Error("No internet connection!"))
            }
        } catch (t : Throwable) {
            when (t) {
                is IOException -> popularMovies.postValue(Resource.Error("Unable to connect!"))
                else -> popularMovies.postValue(Resource.Error("No Signal!"))
            }
        }
    }
}