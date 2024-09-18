package com.harikrish.movieq.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harikrish.movieq.R
import com.harikrish.movieq.adapter.MoviesAdapter
import com.harikrish.movieq.databinding.FragmentDiscoverBinding
import com.harikrish.movieq.ui.MoviesActivity
import com.harikrish.movieq.ui.viewmodel.MoviesViewModel
import com.harikrish.movieq.util.Constants
import com.harikrish.movieq.util.Resource

class DiscoverFragment : Fragment(R.layout.fragment_discover) {

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: FragmentDiscoverBinding

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDiscoverBinding.bind(view)
        moviesViewModel = (activity as MoviesActivity).moviesViewModel
        setupPopularRecycler()

        moviesAdapter.setOnItemClickListener { movie ->
            val action = DiscoverFragmentDirections.actionDiscoverFragmentToDetailsFragment(movie)
            findNavController().navigate(action)

        }

        moviesViewModel.popularMovies.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                        val totalPages =
                            moviesResponse.total_results / Constants.QUERY_PAGE_SIZE + 2

                        Log.d("totalPages", "$totalPages")
                        Log.d("API", "$moviesResponse")
                        Log.d("totalPages2", "${moviesResponse.total_results}")

                        isLastPage = moviesViewModel.popularMoviesPage == totalPages
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
                totalItemCount >= Constants.QUERY_PAGE_SIZE // Adjusted condition
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            Log.d("Pagination", "shouldPaginate: $shouldPaginate")
            if (shouldPaginate) {
                Log.d("Pagination", "Fetching next page...")
                moviesViewModel.getPopularMovies()
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

    private fun setupPopularRecycler() {
        moviesAdapter = MoviesAdapter()
        binding.popularRecyclerView.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(this@DiscoverFragment.scrollListener)
        }
    }
}
