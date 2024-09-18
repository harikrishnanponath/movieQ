package com.harikrish.movieq.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harikrish.movieq.repository.MoviesRepository

class MoviesViewModelFactory(val application: Application, val repository: MoviesRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(application, repository) as T
    }
}