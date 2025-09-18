package com.example.movieseries.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.Movie
import com.example.movieseries.data.Series
import com.example.movieseries.databinding.ItemMovieBinding

class MoviesAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Movie, Boolean) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    private val movies = mutableListOf<Movie>()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = movies[position].id.toLong()

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movie = movie
            binding.isSaved = movie.isSaved

            //Heartclick
            binding.onHeartClick = View.OnClickListener {
                val newState = !(binding.isSaved ?: false)
                binding.isSaved = newState
                binding.executePendingBindings()
                onFavoriteClick(movie, newState)
            }


            binding.root.setOnLongClickListener {
                MovieDetailsBottomSheet.newInstance(movie)
                    .show(fragment.parentFragmentManager, "MovieDetails")
                true
            }

            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    fun setItems(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }



    fun addItems(newMovies: List<Movie>) {
        val start = movies.size
        movies.addAll(newMovies)
        notifyItemRangeInserted(start, newMovies.size)
    }

    fun getItems(): List<Movie> = movies


}
