package com.harikrish.movieq.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.harikrish.movieq.R
import com.harikrish.movieq.databinding.ActivityMoviesBinding
import com.harikrish.movieq.repository.MoviesRepository
import com.harikrish.movieq.ui.viewmodel.MoviesViewModel
import com.harikrish.movieq.ui.viewmodel.MoviesViewModelFactory

class MoviesActivity : AppCompatActivity() {

    lateinit var moviesViewModel: MoviesViewModel
    lateinit var binding: ActivityMoviesBinding

    override fun onBackPressed() {
        // Let NavController handle the back button
        val navController = findNavController(R.id.moviesNavHostFragment)
        if (!navController.navigateUp()) {
            super.onBackPressed()  // Fall back to default behavior
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //instantiating view model
        val moviesRepository = MoviesRepository()
        val viewModelProviderFactory = MoviesViewModelFactory(application, moviesRepository)
        moviesViewModel = ViewModelProvider(this, viewModelProviderFactory).get(MoviesViewModel::class.java)

        //Bottom Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.moviesNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

    }
}