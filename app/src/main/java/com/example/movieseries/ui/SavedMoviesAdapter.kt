package com.example.movieseries.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.MovieEntity
import com.example.movieseries.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso

class SavedMoviesAdapter(
    private val onRemoveClick: (MovieEntity) -> Unit // <-- callback for remove
) : ListAdapter<MovieEntity, SavedMoviesAdapter.MovieViewHolder>(DiffCallback) {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieEntity) {
            binding.textTitle.text = movie.title

            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200${movie.posterPath}")
                .into(binding.imagePoster)

            // Clicking heart will call the remove function
            binding.heartIcon.setOnClickListener {
                onRemoveClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity) =
            oldItem == newItem
    }
}
