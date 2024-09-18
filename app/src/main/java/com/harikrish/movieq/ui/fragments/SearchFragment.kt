package com.harikrish.movieq.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harikrish.movieq.R
import com.harikrish.movieq.adapter.MoviesAdapter
import com.harikrish.movieq.databinding.FragmentSearchBinding
import com.harikrish.movieq.ui.MoviesActivity
import com.harikrish.movieq.ui.viewmodel.MoviesViewModel
import com.harikrish.movieq.util.Constants
import com.harikrish.movieq.util.Constants.Companion.SEARCH_MOVIES_TIME_DELAY
import com.harikrish.movieq.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {


    lateinit var moviesViewModel: MoviesViewModel
    lateinit var moviesAdapter: MoviesAdapter
    lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)


        moviesViewModel = (activity as MoviesActivity).moviesViewModel
        setupSearchRecycler()

        moviesAdapter.setOnItemClickListener { movie ->
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie)
            findNavController().navigate(action)
        }


        var job: Job? = null
        binding.editTextSearch.addTextChangedListener() { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_MOVIES_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        moviesViewModel.searchMovies(editable.toString())
                    }
                }
            }
        }

        moviesViewModel.searchMovies.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success<*> -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                        val totalPages =
                            moviesResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = moviesViewModel.searchMoviesPage == totalPages
                        if (isLastPage) {
                            binding.searchRecycler.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    response.message?.let { message ->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

    }


    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    private fun hideProgressBar() {
        binding.progressBarPagination.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBarPagination.visibility = View.VISIBLE
        isLoading = true
    }


    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                moviesViewModel.searchMovies(binding.editTextSearch.text.toString())
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

    private fun setupSearchRecycler() {
        moviesAdapter = MoviesAdapter()
        binding.searchRecycler.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(this@SearchFragment.scrollListener)

        }
    }
}