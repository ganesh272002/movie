package com.example.movieseries.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.Movie
import com.example.movieseries.data.Series
import com.example.movieseries.databinding.ItemSeriesBinding

class SeriesAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Series, Boolean) -> Unit
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    private val seriesList = mutableListOf<Series>()

    init { setHasStableIds(true) }

    override fun getItemId(position: Int) = seriesList[position].id.toLong()

    inner class SeriesViewHolder(private val binding: ItemSeriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(series: Series) {
            binding.apply {
                this.series = series
                this.isSaved = series.isSaved

                this.onHeartClick = View.OnClickListener {
                    val newState = !(this.isSaved ?: false)
                    this.isSaved = newState
                    executePendingBindings()
                    onFavoriteClick(series.copy(isSaved = newState), newState)
                }

                root.setOnLongClickListener {
                    val bottomSheet = SeriesDetailsBottomSheet.newInstance(series)
                    bottomSheet.show(fragment.parentFragmentManager, "SeriesDetails")
                    true
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val binding = ItemSeriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        holder.bind(seriesList[position])
    }

    override fun getItemCount() = seriesList.size


    fun setItems(newSeries: List<Series>) {
        seriesList.clear()
        seriesList.addAll(newSeries)
        notifyDataSetChanged()
    }


    fun addItems(newSeries: List<Series>) {
        val start = seriesList.size
        seriesList.addAll(newSeries)
        notifyItemRangeInserted(start, newSeries.size)
    }

    fun getItems(): List<Series> = seriesList
}
