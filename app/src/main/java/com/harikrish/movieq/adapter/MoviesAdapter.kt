package com.harikrish.movieq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harikrish.movieq.R
import com.harikrish.movieq.model.Result
import com.harikrish.movieq.util.Constants.Companion.IMAGE_URL

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val movie = differ.currentList[position]

        moviePoster = holder.itemView.findViewById(R.id.movie_poster_image_view)
        movieTitle = holder.itemView.findViewById(R.id.movie_title_text_view)

        holder.itemView.apply {

            val posterPath = movie.poster_path

                Glide.with(this)
                    .load(IMAGE_URL + posterPath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(moviePoster)

            movieTitle.text = movie.title

            setOnClickListener {
                onItemClickListener?.let {
                    it(movie)
                }
            }
        }
    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var moviePoster: ImageView
    lateinit var movieTitle: TextView

}