package com.harikrish.movieq.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harikrish.movieq.R
import com.harikrish.movieq.databinding.FragmentDetailsBinding
import com.harikrish.movieq.ui.MoviesActivity
import com.harikrish.movieq.ui.viewmodel.MoviesViewModel
import com.harikrish.movieq.util.Constants.Companion.IMAGE_URL


class DetailsFragment : Fragment(R.layout.fragment_details) {

    lateinit var moviesViewModel: MoviesViewModel
    private val args: DetailsFragmentArgs by navArgs()
    lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding =FragmentDetailsBinding.bind(view)
        val movie = args.movie

        Log.d("paru", "$movie")

        moviesViewModel = (activity as MoviesActivity).moviesViewModel

        var rating  = movie.vote_average.toFloat() / 2.0
        // Handling potential null values
        binding.detailsTitle.text = movie.title ?: "No Title Available"
        binding.detailsOverview.text = movie.overview
        binding.detailsRatingBar.rating = rating.toFloat()
        binding.detailsReleaseDate.text = "Release Date: "+movie.release_date
        binding.detailsTotalRatings.text = movie.vote_count.toString()

        val posterPath = movie.backdrop_path
        if (posterPath != null) {
            Glide.with(this)
                .load(IMAGE_URL + posterPath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.detailsImage)
        } else {
            // Load a placeholder image if poster_path is null
            Glide.with(this)
                .load(R.drawable.ic_launcher_background) // Ensure you have a placeholder drawable
                .into(binding.detailsImage)
        }



    }

}