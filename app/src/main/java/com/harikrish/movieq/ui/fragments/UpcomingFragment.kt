package com.harikrish.movieq.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harikrish.movieq.R
import com.harikrish.movieq.adapter.MoviesAdapter
import com.harikrish.movieq.databinding.FragmentDiscoverBinding
import com.harikrish.movieq.databinding.FragmentUpcomingBinding
import com.harikrish.movieq.ui.MoviesActivity
import com.harikrish.movieq.ui.viewmodel.MoviesViewModel
import com.harikrish.movieq.util.Constants
import com.harikrish.movieq.util.Resource


class UpcomingFragment : Fragment(R.layout.fragment_upcoming) {

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: FragmentUpcomingBinding

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUpcomingBinding.bind(view)
        moviesViewModel = (activity as MoviesActivity).moviesViewModel
        setupUpcomingRecycler()

        moviesAdapter.setOnItemClickListener { movie ->
            val action = UpcomingFragmentDirections.actionUpcomingFragmentToDetailsFragment(movie)
            findNavController().navigate(action)
        }


        moviesViewModel.upcomingMovies.observe(viewLifecycleOwner, Observer {response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                        val totalPages = moviesResponse.total_results / Constants.QUERY_PAGE_SIZE + 2

                        Log.d("totalPages", "$totalPages")
                        Log.d("API", "$moviesResponse")
                        Log.d("totalPages2","${moviesResponse.total_results}")

                        isLastPage = moviesViewModel.upcomingMoviesPage == totalPages
                        Log.d("totalPages3", "$isLastPage")
                        if (isLastPage) {
                            binding.popularRecyclerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.progressBarPagination.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBarPagination.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible =
                totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            Log.d("Pagination", "shouldPaginate: $shouldPaginate")
            if (shouldPaginate) {
                Log.d("Pagination", "Fetching next page...")
                moviesViewModel.getUpcomingMovies()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setupUpcomingRecycler() {
        moviesAdapter = MoviesAdapter()
        binding.popularRecyclerView.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(this@UpcomingFragment.scrollListener)
        }
    }
}
